import React from "react";
import styled, { css, keyframes } from "styled-components";
import "./App.css";
import dynabyteLogo from "./dynabyte_white.png";
import {
  DiffCamEngine,
  ICapturePayload,
  IDiffCamEngine,
} from "./diff-cam-engine";

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

interface Props {
  isDetected?: boolean;
}

const Title = styled.h1`
  font-family: "Playfair Display", serif;
  margin-top: ${(props: Props) => (props.isDetected ? "25vh" : 0)};
  text-align: center;
  font-size: 4rem;
  color: white;
  ${(props: Props) => props.isDetected && complexMixin};
  order: ${(props: Props) => (props.isDetected ? 1 : 2)};
  opacity: ${(props: Props) => (props.isDetected ? 1 : 0)};
`;

const Logo = styled.img`
  order: ${(props: Props) => (props.isDetected ? 2 : 1)};
  transform: ${(props: Props) =>
    props.isDetected ? "scale(1)" : "scale(0.5)"};
  margin-top: ${(props: Props) => (props.isDetected ? 0 : "10vh")};
`;

export const App = () => {
  const [videoElement, setVideoElement] = React.useState<HTMLElement | null>(
    null
  );
  const [canvasElement, setCanvasElement] = React.useState<HTMLElement | null>(
    null
  );

  const [isDetected, setIsDetected] = React.useState(false);
  // const [dataUrl, setDataUrl] = React.useState<string>('');

  React.useEffect(() => {
    setVideoElement(document.getElementById("video"));
    setCanvasElement(document.getElementById("canvas"));

    const diffCamEngine: IDiffCamEngine = DiffCamEngine();
    const initSuccess = () => {
      diffCamEngine.start();
    };

    const initError = (error: any) => {
      console.log(error);
    };

    const capture = (payload: ICapturePayload) => {
      if (payload.hasMotion) {
        setIsDetected(true);
        console.log(payload.hasMotion, payload.getURL());
      } else {
        setIsDetected(false);
      }
    };

    diffCamEngine.init({
      video: videoElement,
      motionCanvas: canvasElement,
      initSuccessCallback: initSuccess,
      initErrorCallback: initError,
      captureCallback: capture,
      captureIntervalTime: 5000,
    });
  }, [videoElement, canvasElement]);

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
      <span style={{ opacity: 0, position: "fixed" }}>
        <canvas id="canvas"></canvas>
        <video
          id="video"
          width="640"
          height="480"
          muted={true}
          autoPlay={true}
        ></video>
      </span>
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
};

export default App;
