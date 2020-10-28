import axios from 'axios';
import React, { useEffect, useRef, useState } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import CenterContent from '../../ui/CenterContent';
import Spinner from '../../ui/Spinner';

const BigText = styled.p`
  font-size: 3rem;
  font-weight: bold;
`;

const SmallText = styled.p`
  font-size: 1.5rem;
  font-weight: bold;
  margin-top: 10px;
`;

const CaptureFrames = () => {
  const history = useHistory();

  const [hasCollectedImages, setHasCollectedImages] = useState<boolean>(false);
  const intervalRef = useRef<number>(null);

  useEffect(() => {
    const name = history.location.state;
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

          const uploadImages = (images: string[]) => {
            axios
              .post(
                'http://localhost:8080/register',
                { name, images },
                {
                  headers: { 'Content-Type': 'application/json' },
                }
              )
              .then(() => {
                console.log('Uploaded images');
                history.push('/');
              })
              .catch((error) => {
                if (error.response) {
                  const errorData = error.response.data;
                  console.log(errorData);
                  const exceptionClass = errorData.exceptionClass;
                  if (exceptionClass === 'PersonAlreadyInDbException') {
                    history.push({
                      pathname: '/error',
                      state: 'Du är redan registrerad',
                    });
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
      {!hasCollectedImages && (
        <>
          <BigText>Samlar data...</BigText>
          <Spinner />
        </>
      )}
      {hasCollectedImages && (
        <>
          <BigText>Registrering pågår...</BigText>
          <Spinner />
          <SmallText>Det kan ta en liten stund.</SmallText>
        </>
      )}
    </CenterContent>
  );
};

export default CaptureFrames;
