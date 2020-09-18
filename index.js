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
      setTimeout(()=> {
        console.log(faceDetectionsDaniel1);
        console.log(faceDetectionsDaniel2);
        console.log(faceDetectionsNiklas1);
        compareTwoDetectionArrays(faceDetectionsDaniel1, faceDetectionsDaniel1);
        //compareTwoDetectionArrays(faceDetectionsDaniel1, faceDetectionsDaniel2);
        //compareTwoDetectionArrays(faceDetectionsDaniel1, faceDetectionsNiklas1);
      }, 20000);
    })
    .catch(function (err) {
      console.error(err);
    });


    function loadFaceDetectionArrayAndGetDetectionStatistics(folderName, imageHtmlArray, detectionArray) {
        const detectionThreshold = 0.5;
        let nrOfConfidentDetections = 0;
        let nrOfWeakDetections = 0;
        let nrOfNullDetections = 0;

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
              //console.log(detectionResult);
              console.log("(" + folderName + ") Image " + fileNumber + " has detection score: " + detectionResult.detection.score.toFixed(2));

              if(detectionResult.detection.score > detectionThreshold){
                detectionArray.push({fileNumber: fileNumber, folderName: folderName, detectionResult: detectionResult});
                nrOfConfidentDetections++;
              }
              else {
                nrOfWeakDetections++;
              }
          }

          if(i == imageHtmlArray.length - 1){ //Do at the end of the for loop
            const endTime = new Date().getTime();
            const executionTime = endTime - startTime;
            console.log("Confident detections: " + nrOfConfidentDetections);
            console.log("Weak detections: " + nrOfWeakDetections);
            console.log("Null detections: " + nrOfNullDetections);
            console.log("Confident detection percentage: " + (nrOfConfidentDetections/imageHtmlArray.length*100).toFixed(2) + "%")
            console.log("Execution Time: " + executionTime + " ms");
            console.log(folderName + " ------ END DETECTION REPORT ------------");
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
Average euclidean distance: ${(euclideanDistancesArray.reduce((a, b) => a + b, 0)/euclideanDistancesArray.length).toFixed(2)}
Number of comparisons: ${euclideanDistancesArray.length}`;

      console.log(fullComparisonReport);

      console.log(euclideanDistancesArray);
      
    }



  
/*
document.addEventListener('DOMContentLoaded', (event) => {
  console.log('DOM fully loaded and parsed');
});
async function doCompare() {
  console.log("doing compare");
  const results2 = await faceapi.detectAllFaces(image2).withFaceLandmarks().withFaceDescriptors();
  const results3 = await faceapi.detectAllFaces(image3).withFaceLandmarks().withFaceDescriptors();
  const results4 = await faceapi.detectAllFaces(image4).withFaceLandmarks().withFaceDescriptors();
  const results5 = await faceapi.detectAllFaces(image5).withFaceLandmarks().withFaceDescriptors();
  const results6 = await faceapi.detectAllFaces(image6).withFaceLandmarks().withFaceDescriptors();
  const results7 = await faceapi.detectAllFaces(image7).withFaceLandmarks().withFaceDescriptors();
  const results8 = await faceapi.detectAllFaces(image8).withFaceLandmarks().withFaceDescriptors();
  const results9 = await faceapi.detectAllFaces(image9).withFaceLandmarks().withFaceDescriptors();
  const results10 = await faceapi.detectAllFaces(image10).withFaceLandmarks().withFaceDescriptors();
  const results11 = await faceapi.detectAllFaces(image11).withFaceLandmarks().withFaceDescriptors();
  const results12 = await faceapi.detectAllFaces(image12).withFaceLandmarks().withFaceDescriptors();
  const results13 = await faceapi.detectAllFaces(image13).withFaceLandmarks().withFaceDescriptors();
  const results14 = await faceapi.detectAllFaces(image14).withFaceLandmarks().withFaceDescriptors();
  const results15 = await faceapi.detectAllFaces(image15).withFaceLandmarks().withFaceDescriptors();
  const results16 = await faceapi.detectAllFaces(image16).withFaceLandmarks().withFaceDescriptors();
  const results17 = await faceapi.detectAllFaces(image17).withFaceLandmarks().withFaceDescriptors();
  const results18 = await faceapi.detectAllFaces(image18).withFaceLandmarks().withFaceDescriptors();
  const results19 = await faceapi.detectAllFaces(image19).withFaceLandmarks().withFaceDescriptors();
  const results20 = await faceapi.detectAllFaces(image20).withFaceLandmarks().withFaceDescriptors();
  const time1 = new Date().getTime();
  const results1 = await faceapi.detectAllFaces(image1).withFaceLandmarks().withFaceDescriptors();
  for (var i = 0; i < 10000; i++) {
    var dist2 = faceapi.euclideanDistance(results1[0].descriptor, results2[0].descriptor);
    var dist3 = faceapi.euclideanDistance(results1[0].descriptor, results3[0].descriptor);
    var dist4 = faceapi.euclideanDistance(results1[0].descriptor, results4[0].descriptor);
    var dist5 = faceapi.euclideanDistance(results1[0].descriptor, results5[0].descriptor);
    var dist6 = faceapi.euclideanDistance(results1[0].descriptor, results6[0].descriptor);
    var dist7 = faceapi.euclideanDistance(results1[0].descriptor, results7[0].descriptor);
    var dist8 = faceapi.euclideanDistance(results1[0].descriptor, results8[0].descriptor);
    var dist9 = faceapi.euclideanDistance(results1[0].descriptor, results9[0].descriptor);
    var dist10 = faceapi.euclideanDistance(results1[0].descriptor, results10[0].descriptor);
    var dist11 = faceapi.euclideanDistance(results1[0].descriptor, results11[0].descriptor);
    var dist12 = faceapi.euclideanDistance(results1[0].descriptor, results12[0].descriptor);
    var dist13 = faceapi.euclideanDistance(results1[0].descriptor, results13[0].descriptor);
    var dist14 = faceapi.euclideanDistance(results1[0].descriptor, results14[0].descriptor);
    var dist15 = faceapi.euclideanDistance(results1[0].descriptor, results15[0].descriptor);
    var dist16 = faceapi.euclideanDistance(results1[0].descriptor, results16[0].descriptor);
    var dist17 = faceapi.euclideanDistance(results1[0].descriptor, results17[0].descriptor);
    var dist18 = faceapi.euclideanDistance(results1[0].descriptor, results18[0].descriptor);
    var dist19 = faceapi.euclideanDistance(results1[0].descriptor, results19[0].descriptor);
    var dist20 = faceapi.euclideanDistance(results1[0].descriptor, results20[0].descriptor);
  }
  const time2 = new Date().getTime();
  console.log(dist2);
  /*  console.log(dist3);
    console.log(dist4);
    console.log(dist5);
    console.log(dist6);
    console.log(dist7);
    console.log(dist8);
    console.log(dist9);
    console.log(dist10);*/
  /*  console.log(dist11);
    console.log(dist12);
    console.log(dist13);
    console.log(dist14);
    console.log(dist15);
    console.log(dist16);
    console.log(dist17);
    console.log(dist18);
    console.log(dist19);
    console.log(dist20);
  console.log("milliseconds taken:");
  console.log(time2 - time1);
*/