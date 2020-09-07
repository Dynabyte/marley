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
    const detections = await faceapi.detectAllFaces(
      video,
      new faceapi.TinyFaceDetectorOptions()
    );
    console.log(detections);

    if(detections.length > 0 
      && detections[0]._score > 0.5) {
        console.log('Found face');
        title.style.display = 'block';
    }
    else{
      title.style.display = 'none';      
    }

  }, 100);
});
