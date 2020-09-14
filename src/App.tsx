import {
  detectSingleFace,
  loadSsdMobilenetv1Model,
  SsdMobilenetv1Options
} from 'face-api.js';
import React from 'react';
import './App.css';

export const App = () => {
  const [videoElement, setVideoElement] = React.useState<HTMLElement | null>(
    null
  );

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
      .then(async (stream) => {
        (videoElement as HTMLVideoElement).srcObject = stream;
        await startDetection();
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
    <div className='App'>
      <video
        style={{ opacity: 1 }}
        id='video'
        width='720'
        height='560'
        muted={true}
        autoPlay={true}
      ></video>
    </div>
  );
};

const startDetection = async () => {
  const ve = 'video';
  setInterval(async () => {
    
    const detection = await detectSingleFace(
      ve,
      new SsdMobilenetv1Options({ minConfidence: 0.1 })
    );

    if (detection === undefined) {
      console.log('detection is null');
      return;
    }
    if (detection.score > 0.5) {
      console.log('Found face');
    } else {
      console.log('Didn´t find face');
    }
    console.log('Detection score: ' + detection.score.toFixed(2));
  }, 500);
};

export default App;
