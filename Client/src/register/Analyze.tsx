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
    const canvas = document.createElement('canvas') as HTMLCanvasElement;
    const video = document.createElement('video') as HTMLVideoElement;
    const name = history.location.state;
    if (navigator.mediaDevices.getUserMedia) {
      navigator.mediaDevices
        .getUserMedia({
          audio: false,
          video: {
            width: { ideal: 720 },
            height: { ideal: 560 },
          },
        })
        .then((stream) => {
          video.srcObject = stream;
          video.play();

          let imageArray = [];

          const ctx = canvas.getContext('2d');
          for (let i = 0; i < 3; i++) {
            setTimeout(() => {
              canvas.width = video.videoWidth;
              canvas.height = video.videoHeight;
              ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
              const dataURL = canvas.toDataURL();
              console.log('dataURL: ', dataURL);
              imageArray.push(dataURL);
            }, 100);
          }
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
      return () => clearTimeout();
    }
  }, [history]);

  return (
    <Container>{isDone ? <h1>Klart!</h1> : <h1>Analyserar....</h1>}</Container>
  );
};

export default Analyze;
