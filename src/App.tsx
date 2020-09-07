import React from 'react';
import logo from './acnh-marlin-header-1.jpg';
import './App.css';

export const App = () => {
  const [videoElement, setVideoElement] = React.useState<HTMLElement | null>(null);
  React.useEffect(() => {
    setVideoElement(document.getElementById('video'));
    const startMedia = () => {
      if (videoElement === null) {
        console.log("VideoElement doesn't exist");
        return;
      }
      console.log('fasfasf');
      navigator.mediaDevices
        .getUserMedia({
          audio: false,
          video: {
            width: { ideal: 720 },
            height: { ideal: 560 },
          },
        })
        .then((stream) => {
          (videoElement as HTMLVideoElement).srcObject = stream;
        })
        .catch((err) => {
          console.error(err);
        });
    };
    startMedia();
  }, [videoElement]);

  const onPlay: () => void = () => {
    setInterval(async () => {
      if (videoElement === null) {
        console.log("Element doesn't exist");
        return;
      }
      // const detections = await faceApi.detectAllFaces(
      //   videoElement as faceApi.TNetInput,
      //   new faceApi.TinyFaceDetectorOptions()
      // );
      // console.log(detections);

      console.log('ON PLAY');

      // if (detections.length > 0 && detections[0].score > 0.5) {
      //   console.log('Found face');
      //   title.style.display = 'block';
      // } else {
      //   title.style.display = 'none';
      // }
    }, 100);
  };

  // Promise.all([faceApi.nets.tinyFaceDetector.loadFromUri('/models')])
  //   .then(startMedia)
  //   .catch((err) => {
  //     console.error(err);
  //   });

  return (
    <div className='App'>
      <header className='App-header'>
        <img src={logo} className='App-logo' alt='logo' />
        <p>
          <video
            style={{ opacity: 1 }}
            id='video'
            width='720'
            height='560'
            autoPlay={true}
            muted={true}
            onPlay={onPlay}
          ></video>
        </p>
      </header>
    </div>
  );
};

export default App;
