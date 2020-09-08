const video = document.getElementById('video');
const title = document.getElementById('title');
const selectedModel = document.getElementById('selectedModel');
const score = document.getElementById('score');
var interval;

Promise
  .all([
    faceapi.nets.tinyFaceDetector.loadFromUri('/models'),
    faceapi.nets.ssdMobilenetv1.loadFromUri('/models')
  ]).then(startMedia)
  .catch(function (err) {
    console.error(err);
  });

function startMedia() {
  navigator
    .mediaDevices
    .getUserMedia({
      audio: false,
      video: {
        width: { ideal: 720 },
        height: { ideal: 560 },
      },
    })
    .then(function (stream) {
      video.srcObject = stream;
    })
    .catch(function (err) {
      console.error(err);
    });
}

function changeModel(){
    modelName = document.getElementById("model").value;
    selectedModel.innerHTML = modelName;
    score.innerHTML = 'null';
    title.style.opacity = '0';    
    clearInterval(interval);
    if(modelName === "Tiny Face") {
      startDetection(() => new faceapi.TinyFaceDetectorOptions({ scoreThreshold: 0.1 }));
      return;
    }
    if(modelName === "Ssd Mobile Net") {
      startDetection(() => new faceapi.SsdMobilenetv1Options({ minConfidence: 0.1 }));
      return;
    }    
}

function startDetection(getOptions) {
  interval = setInterval(async () => {
    const detection = await faceapi
      .detectSingleFace(
        video, 
        getOptions());
          
    if(detection == null) {
      score.innerHTML = 'null';
      title.style.opacity = '0';
      return;
    }
    if (detection._score > 0.5) {
      title.style.opacity = '1';
    }
    else {
      score.innerHTML = 'null';
      title.style.opacity = '0';
    }
    console.log('Detection score: ' + detection._score.toFixed(2));      
    score.innerHTML = detection._score.toFixed(2);
  }, 500);  
}

video.addEventListener(
  'play', 
  () => startDetection(() => new faceapi.TinyFaceDetectorOptions({ scoreThreshold: 0.1 })), 
  500);
