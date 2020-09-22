/*
Mål:
Få ut singelsnitten, dvs minimum & average euclidean distance per bild i film 1 jämfört med hela film 2 
Kolla avvikelser från genomsnittet, dvs avvikelse från minimum och från average. Kolla även absoluta avvikelserna, dvs endast positiva värden
*/

const divFilm1 = document.getElementById("film1");
const divFilm2 = document.getElementById("film2");

const imagesHtmlFilm1 = [];
const imagesHtmlFilm2 = [];

const faceDetectionsFilm1 = [];
const faceDetectionsFilm2 = [];



const imageNumbersFilm1 = [];
for (var i = 200; i <= 400; i++) {
    imageNumbersFilm1.push(i);
}

imageNumbersFilm1.forEach( (imageNumber, i) => {
    divFilm1.innerHTML += '<img id="film1Image' + i + '" src="images/kontor-kaffe-daniel2/0' + imageNumber + '.jpg"/>';
    imagesHtmlFilm1.push(document.getElementById("film1Image" + i));
})



const imageNumbersFilm2 = [];
for (var i = 150; i <= 350; i++) {
    imageNumbersFilm2.push(i);
}

imageNumbersFilm2.forEach( (imageNumber, i) => {
    divFilm2.innerHTML += '<img id="film2Image' + i + '" src="images/kontor-kaffe-daniel1/0' + imageNumber + '.jpg"/>';
    imagesHtmlFilm2.push(document.getElementById("film2Image" + i));
})

Promise
    .all([
      faceapi.nets.ssdMobilenetv1.loadFromUri('/models'),
      faceapi.nets.faceLandmark68Net.loadFromUri('/models'),
      faceapi.nets.faceRecognitionNet.loadFromUri('/models')
    ]).then(() => {
      loadFaceDetectionArrayAndGetDetectionStatistics("Film1", imagesHtmlFilm1, faceDetectionsFilm1);
      loadFaceDetectionArrayAndGetDetectionStatistics("Film2", imagesHtmlFilm2, faceDetectionsFilm2);

      setTimeout(()=> { //Try to make sure it runs after detectionArrays have loaded
        console.log(faceDetectionsFilm1);
        console.log(faceDetectionsFilm2);
        compareTwoDetectionArrays(faceDetectionsFilm1, faceDetectionsFilm1);
        compareTwoDetectionArrays(faceDetectionsFilm1, faceDetectionsFilm2);
        compareTwoDetectionArrays(faceDetectionsFilm2, faceDetectionsFilm2);
        compareTwoDetectionArrays(faceDetectionsFilm2, faceDetectionsFilm1);
      }, 130000);
    })
    .catch(function (err) {
      console.error(err);
    });
  

      function loadFaceDetectionArrayAndGetDetectionStatistics(folderName, imageHtmlArray, detectionArray) {
        const detectionThreshold = 0.5;
        let nrOfConfidentDetections = 0;
        let nrOfWeakDetections = 0;
        let nrOfNullDetections = 0;
        const allDetectionScores = [];
        const confidentDetectionScores = [];

        const options = new faceapi.SsdMobilenetv1Options({ minConfidence: 0.1 });

        const startTime = new Date().getTime();
        
        imageHtmlArray.forEach(async (htmlImage, i) => {
          const fileNumber = htmlImage.src.substring(htmlImage.src.length - 7, htmlImage.src.length - 4)
          const detectionResult = await faceapi.detectSingleFace(htmlImage, options).withFaceLandmarks().withFaceDescriptor();

          if (detectionResult == null){
              nrOfNullDetections++;
              console.log("(" + folderName + ") Image " + fileNumber + " null detection!")
          }
          else {
              allDetectionScores.push(detectionResult.detection.score);
              console.log("(" + folderName + ") Image " + fileNumber + " has detection score: " + detectionResult.detection.score.toFixed(2));

              if(detectionResult.detection.score > detectionThreshold){
                detectionArray.push({fileNumber: fileNumber, folderName: folderName, detectionResult: detectionResult});
                confidentDetectionScores.push(detectionResult.detection.score);
                nrOfConfidentDetections++;
              }
              else {
                nrOfWeakDetections++;
              }
          }

          if(i == imageHtmlArray.length - 1){ //Do at the end of the for loop
            const endTime = new Date().getTime();
            const executionTime = endTime - startTime;

            const detectionReport =
`Confident detections: ${nrOfConfidentDetections}
Weak detections: ${nrOfWeakDetections}
Null detections: ${nrOfNullDetections}
Confident detection percentage: ${(nrOfConfidentDetections/imageHtmlArray.length*100).toFixed(2)}%
Average confident detection rate: ${(sum(confidentDetectionScores)/nrOfConfidentDetections).toFixed(2)}
Average detection rate: ${(sum(allDetectionScores)/(nrOfWeakDetections+nrOfConfidentDetections)).toFixed(2)}
Execution Time: ${executionTime/1000} seconds
${folderName} ------ END DETECTION REPORT ------------`;
            
            console.log(detectionReport);
          }   
        }) 
    }

    function compareTwoDetectionArrays(detectionArray1, detectionArray2){
      console.log("Running comparisons");
      const allEuclideanDistancesArray = [];
      const singleEuclideanDistancesArray = [];
      let singleIndex = 0;
      let minEuclideanDistance = 1;
      let minEuclideanDistanceImagesInfo = "";

      detectionArray1.forEach(detection1 => {
        const descriptor1 = detection1.detectionResult.descriptor;
        singleEuclideanDistancesArray.push([]);
          detectionArray2.forEach(detection2 => {
            const descriptor2 = detection2.detectionResult.descriptor;
            const euclideanDistance = faceapi.euclideanDistance(descriptor1, descriptor2);
            singleEuclideanDistancesArray[singleIndex].push(euclideanDistance);
            allEuclideanDistancesArray.push(euclideanDistance);

            if(euclideanDistance < minEuclideanDistance 
              && !(detection1.folderName == detection2.folderName && detection1.fileNumber == detection2.fileNumber)){
              minEuclideanDistance = euclideanDistance;
              minEuclideanDistanceImagesInfo = `(${detection1.folderName}) Image ${detection1.fileNumber} and (${detection2.folderName}) Image ${detection2.fileNumber}`
            }

            const imageComparisonReport =
`(${detection1.folderName}) Image ${detection1.fileNumber} - Detection score: ${detection1.detectionResult.detection.score.toFixed(2)}
(${detection2.folderName}) Image ${detection2.fileNumber} - Detection score: ${detection2.detectionResult.detection.score.toFixed(2)}
Euclidean Distance: ${euclideanDistance.toFixed(2)}`;
            
            console.log(imageComparisonReport);
          })
          singleIndex = singleIndex + 1;
      });
      
      const allAverageEuclideanDistance = sum(allEuclideanDistancesArray)/allEuclideanDistancesArray.length;
      const singleEuclideanDistanceAverages = [];
      singleEuclideanDistancesArray.forEach(singleEuclideanDistances =>
        singleEuclideanDistanceAverages.push(
          sum(singleEuclideanDistances)/singleEuclideanDistances.length));

      const singleEuclideanDistanceDiffs = 
        singleEuclideanDistanceAverages
          .map((singleAverage, i) => 
            Math.abs(allAverageEuclideanDistance - singleAverage));

      const fullComparisonReport =
`--- Full Comparison report (${detectionArray1[0].folderName} compared to ${detectionArray2[0].folderName}) ---
Minimum euclidean distance: ${minEuclideanDistance.toFixed(2)} - ${minEuclideanDistanceImagesInfo}
Average all euclidean distance: ${allAverageEuclideanDistance.toFixed(2)}
Max single euclidean distance diff: ${Math.max(...singleEuclideanDistanceDiffs).toFixed(2)}
Average single euclidean distance diff: ${(sum(singleEuclideanDistanceDiffs)/singleEuclideanDistanceDiffs.length).toFixed(2)}
Number of comparisons: ${allEuclideanDistancesArray.length}`;

      console.log(fullComparisonReport);
      
    }

function sum(array){
  return array.reduce((a, b) => a + b, 0);
}