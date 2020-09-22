import React from 'react';
import './App.css';
import { DiffCamEngine } from './diff-cam-engine';
import { ICapturePayload, IDiffCamEngine } from './models/diffCamEngine.models';

export const App = () => {
  // const [dataUrl, setDataUrl] = React.useState<string>('');
  const [hasMotion, setHasMotion] = React.useState<boolean>(false);

  const diffCamEngine: IDiffCamEngine = DiffCamEngine();
  const initSuccess: () => void = () => {
    diffCamEngine.start();
  };

  const initError: (error: any) => void = (error: any) => {
    console.log(error);
  };

  const capture: (payload: ICapturePayload) => void = (
    payload: ICapturePayload
  ) => {
      setHasMotion(payload.hasMotion);
  };
  const videoElement: HTMLVideoElement = document.createElement('video');
  const canvasElement: HTMLCanvasElement = document.createElement('canvas');
  diffCamEngine.init({
    video: videoElement,
    motionCanvas: canvasElement,
    initSuccessCallback: initSuccess,
    initErrorCallback: initError,
    captureCallback: capture,
    captureIntervalTime: 2000,
  });
  return (
    <div className='App'>
      {hasMotion ? <h1>Has motion</h1> : <h1>No motion detected</h1>}
      <span style={{ opacity: 0, position: 'fixed' }}></span>
      {/* <img src={dataUrl} alt='Bilden som skickas' /> */}
    </div>
  );
};

export default App;
