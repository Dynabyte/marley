const video = document.getElementById('video');
const title = document.getElementById('title');
const score = document.getElementById('score');

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

video.addEventListener('play', () => {
  setInterval(async () => {
    const detection = await faceapi
      .detectSingleFace(
        video, 
        new faceapi
          .SsdMobilenetv1Options({ minConfidence: 0.1 }));
          
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
});
