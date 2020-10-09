import axios from 'axios';
import React, { useEffect } from 'react';
import './App.css';
import FaceRegistrationText from './components/FaceRegistrationText';
import { DiffCamEngine } from './diff-cam-engine';
import { ICapturePayload, IDiffCamEngine } from './models/diffCamEngine.models';
import Logo from './shared/Logo';
import Title from './shared/Title';
import dynabyteLogo from './static/images/dynabyte_white.png';

interface IResult {
  isKnownFace?: boolean;
  isFace?: boolean;
  isConfident?: boolean;
  name?: string;
  personId?: string;
}

export const Home = () => {
  const [hasMotion, setHasMotion] = React.useState<boolean>(false);
  const [result, setResult] = React.useState<IResult>({});
  const [isLoading, setIsLoading ] = React.useState<boolean>(true);

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
      setHasMotion(payload.hasMotion);

      if (payload.hasMotion) {
        axios
          .post(
            'http://localhost:8000/predict',
            { image: payload.getURL() },
            {
              headers: { 'Content-Type': 'application/json' },
            }
          )
          .then(({ data }) => {
            setIsLoading(false);
            setResult(data)});
      }
    };

    diffCamEngine.init({
      initSuccessCallback: initSuccess,
      initErrorCallback: initError,
      captureCallback: capture,
      captureIntervalTime: 2000,
    });

    return () => diffCamEngine.stop();
  }, [result]);

  

  const { isKnownFace, isFace, name } = result;

  if(isLoading) {
    return (
      <div className='wrapper'>
      <Logo
      src={dynabyteLogo}
      alt='logo'
      width='200'
      height='80'
    />
    </div>
    );
  }

  return (
    <div className='wrapper'>
      {isKnownFace && (
        <Title hasMotion={hasMotion}>{`Välkommen ${name} till`}</Title>
      )}
      {!isKnownFace && isFace && (
        <>
       <FaceRegistrationText />
       </>
      )}      
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

export default Home;
