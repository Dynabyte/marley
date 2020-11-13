import axios from 'axios';
import React, { useEffect, useRef } from 'react';
import { useHistory } from 'react-router-dom';
import styled, { css, keyframes } from 'styled-components';
import './App.css';
import Modal from './components/Modal';
import FaceRegistrationText from './components/register/FaceRegistrationText';
import useModal from './hooks/useModal';
import Logo from './shared/Logo';
import Title from './shared/Title';
import dynabyteLogo from './static/images/dynabyte_white.png';
import DefaultText from './ui/fonts/DefaultText';
import LargeText from './ui/fonts/LargeText';
import Spinner from './ui/Spinner';
import WhiteButton from './ui/WhiteButton';
import calendarEventLogic from './utility/calendarEventLogic';

interface IResult {
  isKnownFace?: boolean;
  isFace?: boolean;
  name?: string;
  id?: string;
}

const slideIn = keyframes`
 from { opacity: 0; }
`;

const fadeIn = css`
  animation: 2s ease-in-out 0s 1 ${slideIn};
`;

const Container = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;

  .button-modal {
    position: absolute;
    right: 0;
    bottom: 10px;
  }
`;

const ContainerWithFadeIn = styled(Container)`
  ${() => fadeIn};
`;

export const Home = () => {
  const [result, setResult] = React.useState<IResult>({});
  const [paused, setPaused] = React.useState<boolean>(false);
  const [isDeleting, setIsDeleting] = React.useState<boolean>(false);
  const [eventMessage, setEventMessage] = React.useState<string>('');

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

    const updateTimer = () => {
      clearTimeout(activeTimerRef.current);
      startTimer();
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
            sendPredictionRequest(dataURL);
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

    const getDataURL = (img: ImageBitmap) => {
      canvas.width = img.width;
      canvas.height = img.height;
      canvas.getContext('2d').drawImage(img, 0, 0);
      return canvas.toDataURL();
    };

    const sendCalendarRequest = (faceId: string) => {
      axios
        .get(`http://localhost:8080/calendar/${faceId}`)
        .then(({ data }) => {
          const calendarEventMessage = calendarEventLogic(data);
          setEventMessage(calendarEventMessage);
        })
        .catch((error) => {
          if (error.response) {
            const errorData = error.response.data;
            console.log(errorData);
          }
        });
    };

    const sendPredictionRequest = (image: string) => {
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
          console.log(data);
          const requestTime = new Date().getTime() - startTime;
          if (isMounted) {
            setResult(data);
            if (data.isFace) {
              updateTimer();
            }
            if (data.isKnownFace) {
              faceFoundTimer.current = setTimeout(() => {
                predictFace();
              }, process.env.REACT_APP_FOUND_FACE_WAIT_TIME || 2000);
              if (data.hasAllowedCalendar) {
                sendCalendarRequest(data.id);
              }
            } else {
              regulateSpeedAndPredictFace(requestTime);
            }
          }
        })
        .catch((error) => {
          const requestTime = new Date().getTime() - startTime;
          if (error.response) {
            const errorData = error.response.data;
            console.log(errorData);
          }
          regulateSpeedAndPredictFace(requestTime);
        });
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
      <div className='wrapper'>
        <LargeText>Raderar från systemet</LargeText>
        <Spinner />
      </div>
    );
  }

  const handleModal = () => {
    toggle();
    setPaused(true);
  };

  return (
    <div>
      {isKnownFace && (
        <ContainerWithFadeIn>
          <Title>{`Välkommen ${name} till`}</Title>
          <DefaultText>{eventMessage}&nbsp;</DefaultText>
          <WhiteButton className='button-modal' onClick={handleModal}>
            Ta bort mig från systemet
          </WhiteButton>
        </ContainerWithFadeIn>
      )}
      {!isKnownFace && isFace && (
        <ContainerWithFadeIn>
          <FaceRegistrationText />
        </ContainerWithFadeIn>
      )}
      {!isKnownFace && !isFace && (
        <Container>
          <Logo src={dynabyteLogo} width='200' height='80' alt='logo' />
        </Container>
      )}
      <Modal
        isShowing={isShowing}
        hide={() => setIsShowing(false)}
        handleClick={handleClick}
        setPaused={setPaused}
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
