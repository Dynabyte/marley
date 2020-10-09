import React, { useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import axios from 'axios'

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
          let imageBitmaps = [];
          const interval = setInterval(() => {
            const imageCapture = new ImageCapture(myStream.getVideoTracks()[0]);
            if (
              imageCapture.track.readyState === 'live' &&
              imageCapture.track.enabled &&
              !imageCapture.track.muted
            ) {
              imageCapture.grabFrame().then((imageBitmap) => {
                imageBitmaps.push(imageBitmap);
                console.log("captured frame");
                if (imageBitmaps.length === 60) {
                  console.log('Uploading images');
                  clearInterval(interval);

                  //Upload images
                  let images = [];
                  imageBitmaps.forEach(img => {
                    canvas
                      .getContext('2d')
                      .drawImage(img, 0, 0);
                    const dataURL = canvas.toDataURL();
                    images.push(dataURL);
                  });
                  axios
                    .post(
                      'http://localhost:8000/register',
                      { name, images },
                      {
                        headers: { 'Content-Type': 'application/json' },
                      }
                    )
                    .then((res) => {
                      setIsDone(true);
                      console.log('Uploaded images');
                    });
                }
              });
            } else {
              myStream.getTracks().forEach(function (t) {
                t.stop();
              });
              navigator
                .mediaDevices
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
          }, 34);//take 30 frames per second
        });
      return () => clearInterval();
    }
  }, [history]);

  return (
    <Container>{isDone ? <h1>Klart!</h1> : <h1>Analyserar....</h1>}</Container>
  );
};

export default Analyze;
