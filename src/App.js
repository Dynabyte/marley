import {
  detectSingleFace,
  loadSsdMobilenetv1Model,
  SsdMobilenetv1Options
} from 'face-api.js';
import React from 'react';
import './App.css';


function App() {
  const [videoElement, setVideoElement] = React.useState(null);

  React.useEffect(() => {
    setVideoElement(document.getElementById('video'));

    if (videoElement === null) {
      console.log("VideoElement doesn't exist");
      return;
    }
    navigator.mediaDevices
      .getUserMedia({
        audio: false,
        video: {
          width: { ideal: 720 },
          height: { ideal: 560 },
        },
      })
      .then((stream) => {
        videoElement.srcObject = stream;
        startDetection();
      })
      .catch((err) => {
        console.log(err);
      });

    loadSsdMobilenetv1Model('/models')
      .then(() => {
        console.log('model loaded');
      })
      .catch((err) => {
        console.log(err);
      });
  }, [videoElement]);

  return (
    <div className="App">
      <video
        style={{ opacity: 1 }}
        id='video'
        width='720'
        height='560'
        autoPlay={true}
        muted={true}></video>
    </div>
  );
}

async function startDetection() {
  const videoElement = document.getElementById('video');
  setInterval(async () => {
    const detection = await
      detectSingleFace(
        videoElement,
        new SsdMobilenetv1Options({ minConfidence: 0.1 }));

    if (detection == null) {
      console.log('detection is null');
      return;
    }
    if (detection._score > 0.5) {
      console.log('Found face');
    }
    else {
      console.log('Didn´t find face');
    }
    console.log('Detection score: ' + detection._score.toFixed(2));
  }, 500);
}

export default App;
