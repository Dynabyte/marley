import {
  detectSingleFace,
  loadSsdMobilenetv1Model,
  SsdMobilenetv1Options,
  TNetInput
} from 'face-api.js';
import React from 'react';
import './App.css';

export const App = () => {
  const [videoElement, setVideoElement] = React.useState<HTMLElement | null>(
    null
  );

  React.useEffect(() => {
    setVideoElement(document.getElementById('video'));
    videoElement?.addEventListener('play', onPlay, { once: true });
    loadSsdMobilenetv1Model('/models')
      .then(() => startMedia())
      .catch((err) => {
        console.log(err);
      });
  }, [videoElement]);

  const startMedia = () => {
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
      .then((stream: MediaStream) => {
        (videoElement as HTMLVideoElement).srcObject = stream;
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const onPlay: () => void = () => {
    setInterval(async () => {
      if (videoElement === null) {
        console.log("Element doesn't exist");
        return;
      }
      console.log('Hejsan');
      const detection = await detectSingleFace(
        videoElement as TNetInput,
        new SsdMobilenetv1Options({ minConfidence: 0.1 })
      );
      if (detection == null) {
        // score.innerHTML = 'null';
        // title.style.opacity = '0';
        return;
      }
      if (detection.score > 0.5) {
        // title.style.opacity = '1';
      } else {
        // score.innerHTML = 'null';
        // title.style.opacity = '0';
      }
      console.log('Detection score: ' + detection.score.toFixed(2));
      // score.innerHTML = detection.score.toFixed(2);
    }, 10000);
  };

  return (
    <div className='App'>
      <video
        style={{ opacity: 1 }}
        id='video'
        width='720'
        height='560'
        autoPlay={true}
        muted={true}
      ></video>
    </div>
  );
};

export default App;
