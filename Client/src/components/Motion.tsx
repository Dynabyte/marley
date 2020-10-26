import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import { DiffCamEngine } from '../diff-cam-engine';
import {
  ICapturePayload,
  IDiffCamEngine,
} from '../models/diffCamEngine.models';
import Logo from '../shared/Logo';
import dynabyteLogo from '../static/images/dynabyte_white.png';

export const Motion = () => {
  const history = useHistory();
  useEffect(() => {
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
      if (payload.hasMotion) {
        history.push('/');
      }
    };

    diffCamEngine.init({
      initSuccessCallback: initSuccess,
      initErrorCallback: initError,
      captureCallback: capture,
      captureIntervalTime:
        process.env.REACT_APP_MOTION_DETECTION_INTERVAL || 2000,
    });

    return () => diffCamEngine.stop();
  }, [history]);

  return (
    <div className='wrapper'>
      <Logo src={dynabyteLogo} width='200' height='80' alt='logo' />
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

export default Motion;
