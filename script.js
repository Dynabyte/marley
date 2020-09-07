const video = document.getElementById('video');
const title = document.getElementById('title');

Promise.all([
  faceapi.nets.tinyFaceDetector.loadFromUri('/models')
]).then(startMedia)
  .catch(function (err) {
    console.error(err);
  });

function startMedia() {
  navigator.mediaDevices
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

video.addEventListener('play', () => {
  setInterval(async () => {
    const detection = await faceapi.detectSingleFace(
      video, 
      new faceapi.TinyFaceDetectorOptions({ scoreThreshold: 0.1 }));
    if(detection == null) {
      title.style.display = 'none';
      return;
    }
    if (detection._score > 0.5) {
      title.style.display = 'block';
    }
    else {
      title.style.display = 'none';      
    }
    console.log('Detection score: ' + detection._score.toFixed(2));      
  }, 500);
});
