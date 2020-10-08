import axios from 'axios';
import React, { useCallback, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import './App.css';
import { DiffCamEngine } from './diff-cam-engine';
import dynabyteLogo from './dynabyte_white.png';
import { ICapturePayload, IDiffCamEngine } from './models/diffCamEngine.models';
import Logo from './shared/Logo';
import Title from './shared/Title';

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
          .then(({ data }) => setResult(data));
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

  const handleClick = useCallback(() => {
    history.push('/register');
  }, [history]);

  const { isKnownFace, isFace, name } = result;

  return (
    <div className='wrapper'>
      {isKnownFace && (
        <Title hasMotion={hasMotion}>{`Välkommen ${name} till`}</Title>
      )}
      {!isKnownFace && isFace && (
        <>
          <h1>{`Välkommen`}</h1>
          <div>Vi känner inte igen dig sen tidigare. </div>
          <p>Vill du registrera dig?</p>
          <button onClick={handleClick}>JA</button>
        </>
      )}
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

export default Home;
