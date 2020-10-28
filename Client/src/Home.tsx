import axios from 'axios';
import React, { useEffect, useRef } from 'react';
import { useHistory } from 'react-router-dom';
import './App.css';
import Modal from './components/Modal';
import FaceRegistrationText from './components/register/FaceRegistrationText';
import useModal from './hooks/useModal';
import Logo from './shared/Logo';
import Title from './shared/Title';
import dynabyteLogo from './static/images/dynabyte_white.png';
import Spinner from './ui/Spinner';
import WhiteButton from './ui/WhiteButton';

interface IResult {
  isKnownFace?: boolean;
  isFace?: boolean;
  name?: string;
  id?: number;
}

export const Home = () => {
  const [result, setResult] = React.useState<IResult>({});
  const [paused, setPaused] = React.useState<boolean>(false);
  const [isDeleting, setIsDeleting] = React.useState<boolean>(false);

  const timerRef = useRef(null);
  const regulateSpeedTimer = useRef(null);
  const activeTimerRef = useRef(null);
  const faceFoundTimer = useRef(null);
  const { isShowing, setIsShowing, toggle } = useModal();
  const history = useHistory();

  useEffect(() => {
    let isMounted = true;
    const canvas = document.createElement('canvas');
    let myStream: MediaStream;

    const startTimer = () => {
      activeTimerRef.current = setTimeout(() => {
        history.push('/motion');
      }, process.env.REACT_APP_ACTIVE_TIMER_TIMEOUT || 60000);
    };

    const predictFace = () => {
      const imageCapture = new ImageCapture(myStream.getVideoTracks()[0]);
      if (
        imageCapture.track.readyState === 'live' &&
        imageCapture.track.enabled &&
        !imageCapture.track.muted
      ) {
        imageCapture
          .grabFrame()
          .then((imageBitmap) => {
            const dataURL = getDataURL(imageBitmap);
            uploadImage(dataURL);
          })
          .catch(() => console.trace());
      } else {
        myStream.getTracks().forEach(function (t) {
          t.stop();
        });
        navigator.mediaDevices
          .getUserMedia({
            video: {
              width: { ideal: 1920 },
              height: { ideal: 1024 },
            },
          })
          .then(function (stream) {
            myStream = stream;
            console.log('new stream created');
            predictFace();
          });
      }
    };

    const regulateSpeedAndPredictFace = (requestTime: number) => {
      const minimumPredictInterval =
        parseInt(process.env.REACT_APP_MINIMUM_PREDICT_INTERVAL) || 800;
      if (requestTime < minimumPredictInterval) {
        regulateSpeedTimer.current = setTimeout(() => {
          predictFace();
        }, minimumPredictInterval - requestTime);
      } else {
        predictFace();
      }
    };

    const uploadImage = (image: string) => {
      const startTime = new Date().getTime();
      axios
        .post(
          'http://localhost:8080/predict',
          { image },
          {
            headers: { 'Content-Type': 'application/json' },
          }
        )
        .then(({ data }) => {
          const requestTime = new Date().getTime() - startTime;
          console.log(`Request time: ${requestTime} ms`);
          if (isMounted) {
            setResult(data);
            if (data.isFace) {
              faceFoundTimer.current = setTimeout(() => {
                predictFace();
                updateTimer();
              }, process.env.REACT_APP_FOUND_FACE_WAIT_TIME || 2000);
            } else {
              regulateSpeedAndPredictFace(requestTime);
            }
          }
        })
        .catch((error) => {
          const requestTime = new Date().getTime() - startTime;
          console.log(`Request time: ${requestTime} ms`);
          if (error.response) {
            const errorData = error.response.data;
            console.log(errorData);
          }
          regulateSpeedAndPredictFace(requestTime);
        });
    };

    const updateTimer = () => {
      clearTimeout(activeTimerRef.current);
      startTimer();
    };

    const getDataURL = (img: ImageBitmap) => {
      canvas.width = img.width;
      canvas.height = img.height;
      canvas.getContext('2d').drawImage(img, 0, 0);
      return canvas.toDataURL();
    };

    if (navigator.mediaDevices.getUserMedia) {
      navigator.mediaDevices
        .getUserMedia({
          audio: false,
          video: {
            width: { ideal: 720 },
            height: { ideal: 560 },
          },
        })
        .then((stream: MediaStream) => {
          myStream = stream;
          if (!paused) {
            predictFace();
          }
        });
    }

    startTimer();

    return () => {
      myStream.getTracks().forEach(function (t) {
        t.stop();
      });
      clearTimeout(timerRef.current);
      clearTimeout(activeTimerRef.current);
      clearTimeout(regulateSpeedTimer.current);
      clearTimeout(faceFoundTimer.current);
      isMounted = false;
    };
  }, [paused, history]);

  const { isKnownFace, isFace, name, id } = result;

  const handleClick = () => {
    setIsDeleting(true);

    axios
      .delete(`http://localhost:8080/delete/${id}`, {
        headers: { 'Content-Type': 'application/json' },
      })
      .then(() => {
        console.log('Deleted from system!');
        timerRef.current = setTimeout(() => {
          setIsDeleting(false);
          setResult({});
          setPaused(false);
        }, 2000);
      })
      .catch((error) => {
        setIsDeleting(false);
        setResult({});
        setPaused(false);
        if (error.response) {
          const errorData = error.response.data;
          console.log(errorData);
        }
      });
  };

  if (isDeleting) {
    return (
      <div className='wrapper' style={{ fontSize: '3rem', fontWeight: 'bold' }}>
        <p>Raderar från systemet....</p>
        <Spinner />
      </div>
    );
  }

  const handleModal = () => {
    toggle();
    setPaused(true);
  };

  return (
    <div className='wrapper'>
      {isKnownFace && (
        <>
          <Title>{`Välkommen ${name} till`}</Title>
          <WhiteButton className='button-default' onClick={handleModal}>
            Ta bort mig från systemet
          </WhiteButton>
          <Modal
            isShowing={isShowing}
            hide={() => setIsShowing(false)}
            handleClick={handleClick}
            setPaused={setPaused}
          />
        </>
      )}
      {!isKnownFace && isFace && <FaceRegistrationText />}
      {!isKnownFace && !isFace && (
        <Logo src={dynabyteLogo} width='200' height='80' alt='logo' />
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
