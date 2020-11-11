import axios from 'axios';
import React, { useEffect, useRef, useState } from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import CenterContent from '../../ui/CenterContent';
import LargeText from '../../ui/fonts/LargeText';
import SmallText from '../../ui/fonts/SmallText';
import Spinner from '../../ui/Spinner';
import ErrorMessage from '../ErrorMessage';
import { registrationData } from './PositionInformation';

const CaptureFrames = () => {
  const history = useHistory();

  const [hasCollectedImages, setHasCollectedImages] = useState<boolean>(false);
  const [hasError, setHasError] = useState<boolean>(false);

  const intervalRef = useRef<number>(null);
  const location = useLocation<registrationData>();
  const { name, authCode } = location.state;

  useEffect(() => {
    const canvas: HTMLCanvasElement = document.createElement('canvas');
    let myStream: MediaStream;
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
          let imageBitmaps: ImageBitmap[] = [];
          intervalRef.current = setInterval(() => {
            const imageCapture = new ImageCapture(myStream.getVideoTracks()[0]);
            if (
              imageCapture.track.readyState === 'live' &&
              imageCapture.track.enabled &&
              !imageCapture.track.muted
            ) {
              imageCapture
                .grabFrame()
                .then((imageBitmap) => {
                  imageBitmaps.push(imageBitmap);
                  if (imageBitmaps.length === 60) {
                    setHasCollectedImages(true);
                    console.log('Uploading images');
                    myStream.getTracks().forEach(function (t) {
                      t.stop();
                    });
                    clearInterval(intervalRef.current);

                    const base64images = getDataURL(imageBitmaps);
                    uploadImages(base64images);
                  }
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
                });
            }
          }, 34); //take 30 frames per second

          const getDataURL = (imageBitmaps: ImageBitmap[]) => {
            let base64images: string[] = [];
            imageBitmaps.forEach((img) => {
              canvas.width = img.width;
              canvas.height = img.height;
              canvas.getContext('2d').drawImage(img, 0, 0);
              const dataURL: string = canvas.toDataURL();
              base64images.push(dataURL);
            });
            return base64images;
          };

          const saveGoogleCalendarTokens = (faceId: string) => {
            axios
              .post(
                'http://localhost:8080/calendar/tokens',
                { faceId, authCode },
                {
                  headers: { 'Content-Type': 'application/json' },
                }
              )
              .catch((error) => {
                if (error.response) {
                  const errorData = error.response.data;
                  console.log(errorData);
                }
              })
              .finally(() => {
                history.push('/');
              });
          };

          const uploadImages = (images: string[]) => {
            axios
              .post(
                'http://localhost:8080/register',
                { name, images },
                {
                  headers: { 'Content-Type': 'application/json' },
                }
              )
              .then(({ data }) => {
                console.log('Uploaded images');
                console.log(`Auth code: ${authCode}`);
                
                if(authCode !== undefined){
                  saveGoogleCalendarTokens(data);
                }
                else{
                  history.push('/');
                }
              })
              .catch((error) => {
                if (error.response) {
                  const errorData = error.response.data;
                  console.log(errorData);
                  const exceptionClass = errorData.exceptionClass;
                  if (exceptionClass === 'PersonAlreadyInDbException') {
                    setHasError(true);
                  }
                }
              });
          };
        });
    }

    return () => {
      clearInterval(intervalRef.current);
    };
  }, [history]);

  return (
    <CenterContent>
      {!hasCollectedImages && !hasError && (
        <>
          <LargeText>Samlar data</LargeText>
          <Spinner />
        </>
      )}
      {hasCollectedImages && !hasError && (
        <>
          <LargeText>Registrering pågår</LargeText>
          <Spinner />
          <SmallText>Det kan ta en liten stund</SmallText>
        </>
      )}
      {hasError && <ErrorMessage message='Du är redan registrerad' />}
    </CenterContent>
  );
};

export default CaptureFrames;
