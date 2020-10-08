import React, { useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100vh;
  align-items: center;
`;

const Analyze = () => {
  const history = useHistory();

  const [isDone, setIsDone] = useState(false);

  useEffect(() => {
    const name = history.location.state;
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
          let imageArray = [];
          setInterval(() => {
            const imageCapture = new ImageCapture(myStream.getVideoTracks()[0]);
            if (
              imageCapture.track.readyState === 'live' &&
              imageCapture.track.enabled &&
              !imageCapture.track.muted
            ) {
              imageCapture.grabFrame().then((imageBitmap) => {
                canvas.getContext('2d').drawImage(imageBitmap, 0, 0);
                const dataURL = canvas.toDataURL();
                imageArray.push(dataURL);
                console.log(dataURL);
              });
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
          }, 2000);

          // axios
          //   .post(
          //     'http://localhost:8000/register',
          //     { name, images: imageArray },
          //     {
          //       headers: { 'Content-Type': 'application/json' },
          //     }
          //   )
          //   .then((res) => setIsDone(true));
        });
      return () => clearInterval();
    }
  }, [history]);

  return (
    <Container>{isDone ? <h1>Klart!</h1> : <h1>Analyserar....</h1>}</Container>
  );
};

export default Analyze;
