/*
Mål:
Testa alla bilder mot alla andra bilder för att veta hur många frames framåt och bakåt som ger bra resultat, gärna mycket statistik:
Alla scores (euclydian distance), medeltal av scores, träff/inte träff...
Hur många gånger får man inte ut ett ansikte alls (inga landmarks)

Testa dels inbördes för varje bild i en film, sen jämför en film med en annan (samma person)
*/

const divDaniel1 = document.getElementById("daniel1");
const divDaniel2 = document.getElementById("daniel2");
const divNiklas1 = document.getElementById("niklas1");

const imagesHtmlDaniel1 = [];
const imagesHtmlDaniel2 = [];
const imagesHtmlNiklas1 = [];

const faceDetectionsDaniel1 = [];
const faceDetectionsDaniel2 = [];
const faceDetectionsNiklas1 = [];



const imageNumbersDaniel1 = [];
for (var i = 145; i <= 375; i++) {
    imageNumbersDaniel1.push(i);
}



imageNumbersDaniel1.forEach( (imageNumber, i) => {
    divDaniel1.innerHTML += '<img id="daniel1Image' + i + '" src="images/daniel-vardagsrum1/0' + imageNumber + '.jpg"/>';
    imagesHtmlDaniel1.push(document.getElementById("daniel1Image" + i));
})

const imageNumbersDaniel2 = [];
for (var i = 129; i <= 256; i++) {
    imageNumbersDaniel2.push(i);
}

imageNumbersDaniel2.forEach( (imageNumber, i) => {
    divDaniel2.innerHTML += '<img id="daniel2Image' + i + '" src="images/daniel-vardagsrum2/0' + imageNumber + '.jpg"/>';
    imagesHtmlDaniel2.push(document.getElementById("daniel2Image" + i));
})

const imageNumbersNiklas1 = [];
for (var i = 125; i <= 284; i++) {
    imageNumbersNiklas1.push(i);
}



imageNumbersNiklas1.forEach( (imageNumber, i) => {
    divNiklas1.innerHTML += '<img id="niklas1Image' + i + '" src="images/niklas-ingång-vallentuna/0' + imageNumber + '.jpg"/>';
    imagesHtmlNiklas1.push(document.getElementById("niklas1Image" + i));
})

Promise
    .all([
      faceapi.nets.ssdMobilenetv1.loadFromUri('/models'),
      faceapi.nets.faceLandmark68Net.loadFromUri('/models'),
      faceapi.nets.faceRecognitionNet.loadFromUri('/models')
    ]).then(() => {
      loadFaceDetectionArrayAndGetDetectionStatistics("Daniel1", imagesHtmlDaniel1, faceDetectionsDaniel1);
      loadFaceDetectionArrayAndGetDetectionStatistics("Daniel2", imagesHtmlDaniel2, faceDetectionsDaniel2);
      loadFaceDetectionArrayAndGetDetectionStatistics("Niklas1", imagesHtmlNiklas1, faceDetectionsNiklas1);
      setTimeout(()=> { //Try to make sure it runs after detectionArrays have loaded
        console.log(faceDetectionsDaniel1);
        console.log(faceDetectionsDaniel2);
        console.log(faceDetectionsNiklas1);
        compareTwoDetectionArrays(faceDetectionsDaniel1, faceDetectionsDaniel1);
        //compareTwoDetectionArrays(faceDetectionsDaniel1, faceDetectionsDaniel2);
        //compareTwoDetectionArrays(faceDetectionsDaniel1, faceDetectionsNiklas1);
      }, 30000);
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
      const euclideanDistancesArray = [];
      let minEuclideanDistance = 1;
      let minEuclideanDistanceImagesInfo = "";

      detectionArray1.forEach(detection1 => {
        const descriptor1 = detection1.detectionResult.descriptor;

          detectionArray2.forEach(detection2 => {
            const descriptor2 = detection2.detectionResult.descriptor;
            const euclideanDistance = faceapi.euclideanDistance(descriptor1, descriptor2);
            
            euclideanDistancesArray.push(euclideanDistance);

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
      });
      
      const fullComparisonReport =
`--- Full Comparison report (${detectionArray1[0].folderName} compared to ${detectionArray2[0].folderName}) ---
Minimum euclidean distance: ${minEuclideanDistance.toFixed(2)} - ${minEuclideanDistanceImagesInfo}
Average euclidean distance: ${(sum(euclideanDistancesArray)/euclideanDistancesArray.length).toFixed(2)}
Number of comparisons: ${euclideanDistancesArray.length}`;

      console.log(fullComparisonReport);

      console.log(euclideanDistancesArray);
      
    }

function sum(array){
  return array.reduce((a, b) => a + b, 0);
}