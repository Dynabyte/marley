import moment from "moment";
import React from "react";
import { DiffCamEngine } from "./diff-cam-engine";
import dynabyteLogo from "./dynabyte_white.png";
import { ICapturePayload, IDiffCamEngine } from "./models/diffCamEngine.models";
import Logo from "./shared/Logo";
import Title from "./shared/Title";

export const App = () => {
  const [hasMotion, setHasMotion] = React.useState<boolean>(false);

  const videoElement: HTMLVideoElement = document.createElement('video');
  const canvasElement: HTMLCanvasElement = document.createElement('canvas');

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
    console.log(payload.getURL(), moment().second());
    if (payload.hasMotion) {
      console.log(payload.getURL());
    }
  };

  diffCamEngine.init({
    video: videoElement,
    motionCanvas: canvasElement,
    initSuccessCallback: initSuccess,
    initErrorCallback: initError,
    captureCallback: capture,
    captureIntervalTime: 10000,
  });
  return (
    <div className='wrapper'>
      <Title hasMotion={hasMotion}>Välkommen till</Title>
      <Logo
        src={dynabyteLogo}
        alt='logo'
        width='200'
        height='80'
        hasMotion={hasMotion}
      />
      <footer>
        <span>
          Photo by{' '}
          <a href='https://unsplash.com/@freetousesoundscom?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText'>
            Free To Use Sounds
          </a>{' '}
          on{' '}
          <a href='https://unsplash.com/s/photos/grass?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText'>
            Unsplash
          </a>
        </span>
      </footer>
    </div>
  );
};

export default App;
