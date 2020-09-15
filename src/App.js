import {
  detectSingleFace,
  loadSsdMobilenetv1Model,
  SsdMobilenetv1Options,
} from "face-api.js";
import React from "react";
import styled, { css, keyframes } from "styled-components";
import "./App.css";
import dynabyteLogo from "./dynabyte_white.png";

const slideIn = keyframes`
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
`;

const complexMixin = css`
  animation: 2s ease-in-out 0s 1 ${slideIn};
`;

const Title = styled.h1`
  font-family: "Playfair Display", serif;
  margin-top: ${(props) => (props.isDetected ? "25vh" : 0)};
  text-align: center;
  font-size: 4rem;
  color: white;
  ${(props) => props.isDetected && complexMixin};
  order: ${(props) => (props.isDetected ? 1 : 2)};
  opacity: ${(props) => (props.isDetected ? 1 : 0)};
`;

const Logo = styled.img`
  order: ${(props) => (props.isDetected ? 2 : 1)};
  transform: ${(props) => (props.isDetected ? "scale(1)" : "scale(0.5)")};
  margin-top: ${(props) => (props.isDetected ? 0 : "10vh")};
`;

function App() {
  const [videoElement, setVideoElement] = React.useState(null);
  const [isDetected, setIsDetected] = React.useState(false);

  React.useEffect(() => {
    setVideoElement(document.getElementById("video"));

    if (videoElement === null) {
      console.log("VideoElement doesn't exist");
      return;
    }
    navigator.mediaDevices
      .getUserMedia({
        audio: false,
        video: {
          width: { ideal: 720 },
          height: { ideal: 560 },
        },
      })
      .then((stream) => {
        videoElement.srcObject = stream;
        startDetection();
      })
      .catch((err) => {
        console.log(err);
      });

    loadSsdMobilenetv1Model("/models")
      .then(() => {
        console.log("model loaded");
      })
      .catch((err) => {
        console.log(err);
      });
  }, [videoElement]);

  async function startDetection() {
    const videoElement = document.getElementById("video");
    setInterval(async () => {
      const detection = await detectSingleFace(
        videoElement,
        new SsdMobilenetv1Options({ minConfidence: 0.1 })
      );

      if (detection == null) {
        setIsDetected(false);
        console.log("detection is null");
        return;
      }
      if (detection._score > 0.5) {
        setIsDetected(true);
        console.log("Found face");
      } else {
        setIsDetected(false);
        console.log("Didn´t find face");
      }
      console.log("Detection score: " + detection._score.toFixed(2));
    }, 500);
  }

  return (
    <div className="wrapper">
      <Title isDetected={isDetected}>Välkommen till</Title>
      <Logo
        src={dynabyteLogo}
        alt="logo"
        width="200"
        height="80"
        isDetected={isDetected}
      />
      <video
        style={{ opacity: 0 }}
        id="video"
        width="720"
        height="560"
        autoPlay={true}
        muted={true}
      ></video>
      <footer>
        <span>
          Photo by{" "}
          <a href="https://unsplash.com/@freetousesoundscom?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">
            Free To Use Sounds
          </a>{" "}
          on{" "}
          <a href="https://unsplash.com/s/photos/grass?utm_source=unsplash&amp;utm_medium=referral&amp;utm_content=creditCopyText">
            Unsplash
          </a>
        </span>
      </footer>
    </div>
  );
}

export default App;
