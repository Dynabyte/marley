import { faCog } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import React, { useEffect, useRef } from 'react';
import { useHistory } from 'react-router-dom';
import styled, { css, keyframes } from 'styled-components';
import './App.css';
import FaceRegistrationText from './components/register/FaceRegistrationText';
import Modal from './components/SettingsModal';
import useModal from './hooks/useModal';
import Logo from './shared/Logo';
import Title from './shared/Title';
import dynabyteLogo from './static/images/dynabyte_white.png';
import DefaultText from './ui/fonts/DefaultText';
import LargeText from './ui/fonts/LargeText';
import Spinner from './ui/Spinner';
import calendarEventLogic from './utility/calendarEventLogic';

interface IResult {
  isKnownFace?: boolean;
  isFace?: boolean;
  name?: string;
  id?: string;
  hasAllowedCalendar?: boolean;
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
`;

const SettingsButton = styled.button`
  position: absolute;
  right: 30px;
  bottom: 30px;
  background: none;
  border: none;
  outline: inherit;
`;

const ContainerWithFadeIn = styled(Container)`
  ${() => fadeIn};
`;

export const Home = () => {
  const [result, setResult] = React.useState<IResult>({});
  const faceIdRef = useRef(null);
  const [paused, setPaused] = React.useState<boolean>(false);
  const [isDeleting, setIsDeleting] = React.useState<boolean>(false);
  const [eventMessage, setEventMessage] = React.useState<string>('');

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
          const requestTime = new Date().getTime() - startTime;
          if (isMounted) {
            if (faceIdRef.current !== data.id || !data.hasAllowedCalendar) {
              setEventMessage('');
            }
            setResult(data);
            faceIdRef.current = data.id;

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
      clearTimeout(activeTimerRef.current);
      clearTimeout(regulateSpeedTimer.current);
      clearTimeout(faceFoundTimer.current);
      isMounted = false;
    };
  }, [paused, history]);

  const { isKnownFace, isFace, name, id, hasAllowedCalendar } = result;

  if (isDeleting) {
    return (
      <div className='wrapper'>
        <LargeText>Raderar från systemet</LargeText>
        <Spinner />
      </div>
    );
  }

  const handleSettingsModal = () => {
    toggle();
    setPaused(true);
  };

  return (
    <div>
      {isKnownFace && (
        <>
          <ContainerWithFadeIn>
            <Title>{`Välkommen ${name} till`}</Title>
            <DefaultText>{eventMessage}&nbsp;</DefaultText>
            <SettingsButton onClick={handleSettingsModal}>
              <FontAwesomeIcon icon={faCog} color='white' size='4x' />
            </SettingsButton>
          </ContainerWithFadeIn>
          <Modal
            isShowing={isShowing}
            hide={() => setIsShowing(false)}
            faceId={id}
            hasAllowedCalendar={hasAllowedCalendar}
            setPaused={setPaused}
            setIsDeleting={setIsDeleting}
            setResult={setResult}
          />
        </>
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
