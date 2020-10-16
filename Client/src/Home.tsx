import axios from 'axios';
import React, { useEffect, useRef } from 'react';
import './App.css';
import FaceRegistrationText from './components/register/FaceRegistrationText';
import Logo from './shared/Logo';
import Title from './shared/Title';
import dynabyteLogo from './static/images/dynabyte_white.png';

interface IResult {
  isKnownFace?: boolean;
  isFace?: boolean;
  name?: string;
}

export const Home = () => {
  const [result, setResult] = React.useState<IResult>({});
  const [isLoading, setIsLoading] = React.useState<boolean>(true);
  const intervalRef = useRef(null);

  useEffect(() => {
    let isMounted = true;
    const canvas = document.createElement('canvas');
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
                });
            }
          }, 800);
        });

      const uploadImage = (image: string) => {
        axios
          .post(
            'http://localhost:8080/predict',
            { image },
            {
              headers: { 'Content-Type': 'application/json' },
            }
          )
          .then(({ data }) => {
            if (isMounted) {
              setIsLoading(false);
              setResult(data);
            }
          })
          .catch((error) => {
            if( error.response ){
              const errorData = error.response.data;
              console.log(errorData);      
          }
          })
      };

      const getDataURL = (img: ImageBitmap) => {
        canvas.width = img.width;
        canvas.height = img.height;
        canvas.getContext('2d').drawImage(img, 0, 0);
        return canvas.toDataURL();
      };
    }
    return () => {
      myStream.getTracks().forEach(function (t) {
        t.stop();
      });
      clearInterval(intervalRef.current);
      isMounted = false;
    };
  }, []);

  const { isKnownFace, isFace, name } = result;

  if (isLoading) {
    return (
      <div className='wrapper'>
        <Logo src={dynabyteLogo} alt='logo' width='200' height='80' />
      </div>
    );
  }

  return (
    <div className='wrapper'>
      {isKnownFace && <Title>{`Välkommen ${name} till`}</Title>}
      {!isKnownFace && isFace && <FaceRegistrationText />}

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
