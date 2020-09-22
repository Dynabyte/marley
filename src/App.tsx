import React from 'react';
import './App.css';
import { DiffCamEngine, ICapturePayload, IDiffCamEngine } from './diff-cam-engine';

export const App = () => {
  const [videoElement, setVideoElement] = React.useState<HTMLElement | null>(
    null
  );
  const [canvasElement, setCanvasElement] = React.useState<HTMLElement | null>(
    null
  );
  // const [dataUrl, setDataUrl] = React.useState<string>('');

  React.useEffect(() => {
    setVideoElement(document.getElementById('video'));
    setCanvasElement(document.getElementById('canvas'));

    const diffCamEngine: IDiffCamEngine = DiffCamEngine();
    const initSuccess = () => {
      diffCamEngine.start();
    };

    const initError = (error: any) => {
      console.log(error);
    };

    const capture = (payload: ICapturePayload) => {
      if (payload.hasMotion) {
        console.log(payload.hasMotion, payload.getURL());
      }
    };

    diffCamEngine.init({
      video: videoElement,
      motionCanvas: canvasElement,
      initSuccessCallback: initSuccess,
      initErrorCallback: initError,
      captureCallback: capture,
      captureIntervalTime: 5000,
    });
  }, [videoElement, canvasElement]);

  return (
    <div className='App'>
      <span style={{ opacity: 0, position: 'fixed' }}>
        <canvas id='canvas'></canvas>
        <video
          id='video'
          width='640'
          height='480'
          muted={true}
          autoPlay={true}
        ></video>
      </span>
    </div>
  );
};

export default App;
