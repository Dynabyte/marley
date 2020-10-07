import axios from 'axios';
import React, { createRef, RefObject, useEffect, useState } from 'react';
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
  const name = history.location.state;

  const [isDone, setIsDone] = useState(false);

  const videoRef = createRef() as RefObject<any>;
  const canvas = document.createElement('canvas') as HTMLCanvasElement;

  useEffect(() => {
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
          videoRef.current.srcObject = stream;

          const ctx = canvas.getContext('2d');
          let imageArray = [];

          let numberOfCalls = 0;
          const interval = setInterval(() => {
            canvas.width = 720;
            canvas.height = 560;
            ctx.drawImage(videoRef.current, 0, 0, 720, 560);
            // Add to array
            const dataURL = canvas.toDataURL();
            imageArray.push(dataURL);
            //setImageArray((imageArray) => [...imageArray, dataURL]);
            if (++numberOfCalls === 3) {
              clearInterval(interval);
              axios
                .post(
                  'http://localhost:8000/register',
                  { name, images: imageArray },
                  {
                    headers: { 'Content-Type': 'application/json' },
                  }
                )
                .then((res) =>
                  res.status === 200 ? setIsDone(true) : setIsDone(false)
                );
            }
          }, 100);
        });
    }
  }, [canvas, videoRef, name]);

  return (
    <Container>
      {isDone ? <h1>Klart!</h1> : <h1>Analyserar....</h1>}
      <video autoPlay ref={videoRef} style={{ display: 'none' }}></video>
    </Container>
  );
};

export default Analyze;
