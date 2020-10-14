import axios from 'axios';
import React, { useEffect, useRef, useState } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100vh;
  align-items: center;
  font-size: 3rem;
  font-weight: bold;
`;

const Button = styled.button`
    padding: 15px;
    margin: 0 10px;
    letter-spacing: 1px;
    border-radius: 5px;
    border: 1px solid #737b8d;
    font-size: 1rem;
    font-weight: 500;
    opacity: 0.8;

  `;

  const Text = styled.div`
    text-align: center;
    max-width: 70%;
  `;

const CaptureFrames = () => {
  const history = useHistory();
  
  const [isVisible, setIsVisible] = useState<boolean>(false);
  const intervalRef = useRef(null);
  
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
         intervalRef.current = setInterval(() => {
            const imageCapture = new ImageCapture(myStream.getVideoTracks()[0]);
            if (
              imageCapture.track.readyState === 'live' &&
              imageCapture.track.enabled &&
              !imageCapture.track.muted
            ) {
              imageCapture.grabFrame().then((imageBitmap) => {
                imageBitmaps.push(imageBitmap);
                if (imageBitmaps.length === 60) {
                  setIsVisible(true);
                  console.log('Uploading images');
                  myStream.getTracks().forEach(function (t) {
                    t.stop(); }); 
                  clearInterval(intervalRef.current); 
                  const base64images = getDataURL(imageBitmaps);
                  uploadImages(base64images);
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
                  console.log('new stream created capture');
                });
            }
          }, 34); //take 30 frames per second

          const getDataURL = (imageBitmaps: ImageBitmap[]) => {
            let base64images = [];
            imageBitmaps.forEach(img => {
              canvas.width = img.width;
              canvas.height = img.height;
              canvas
                .getContext('2d')
                .drawImage(img, 0, 0);
              const dataURL = canvas.toDataURL();
              base64images.push(dataURL);
            });
            return base64images;

          }

      const uploadImages = (images: string[]) => {
          axios
            .post(
              'http://localhost:8000/register',
              { name, images },
              {
                headers: { 'Content-Type': 'application/json' },
              }
            )
            .then(() => {
              console.log('Uploaded images');
            }).catch(error => console.error(error));
      }
      
    });
  }
  return () => clearInterval(intervalRef.current);
  }, [history]);

  if(isVisible) {
    const timeoutId = setTimeout(() => {
      setIsVisible(false);
      clearTimeout(timeoutId);
      history.push('/');
    }, 60000);
  }

  const handleClick = () => {
    setIsVisible(false);
    clearTimeout();
    history.push('/');
  }

  return (
    <Container>
      {!isVisible && <div>Laddar...</div>}
 {isVisible && (
 <Text>
   <p>Tack! Bilderna har tagits emot för registrering. </p> 
   <p>Det kan ta någon minut innan registreringen har gått igenom och du kan bli igenkänd i systemet.</p>
   <Button onClick={handleClick}>KLAR</Button>
 </Text>)}
    </Container>
   
  );
};

export default CaptureFrames;
