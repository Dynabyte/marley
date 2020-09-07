const video = document.getElementById('video');

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

startMedia();
