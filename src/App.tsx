import React from "react";
// import styled, { css, keyframes } from "styled-components";
import "./App.css";
// import dynabyteLogo from "./dynabyte_white.png";
import { DiffCamEngine } from "./diff-cam-engine";
import { ICapturePayload, IDiffCamEngine } from "./models/diffCamEngine.models";

//TODO: Use these styling when we are ready.
// const slideIn = keyframes`
//   0% {
//     opacity: 0;
//   }
//   50% {
//     opacity: 1;
//   }
// `;

// const complexMixin = css`
//   animation: 2s ease-in-out 0s 1 ${slideIn};
// `;

// interface Props {
//   hasMotion?: boolean;
// }

// const Title = styled.h1`
//   font-family: "Playfair Display", serif;
//   margin-top: ${(props: Props) => (props.hasMotion ? "25vh" : 0)};
//   text-align: center;
//   font-size: 4rem;
//   color: white;
//   ${(props: Props) => props.hasMotion && complexMixin};
//   order: ${(props: Props) => (props.hasMotion ? 1 : 2)};
//   opacity: ${(props: Props) => (props.hasMotion ? 1 : 0)};
// `;

// const Logo = styled.img`
//   order: ${(props: Props) => (props.hasMotion ? 2 : 1)};
//   transform: ${(props: Props) => (props.hasMotion ? "scale(1)" : "scale(0.5)")};
//   margin-top: ${(props: Props) => (props.hasMotion ? 0 : "10vh")};
// `;

export const App = () => {
  const [dataUrl, setDataUrl] = React.useState<string>("");
  const [hasMotion, setHasMotion] = React.useState<boolean>(false);

  const diffCamEngine: IDiffCamEngine = DiffCamEngine();
  const initSuccess: () => void = () => {
    diffCamEngine.start();
  };

  const initError: (error: any) => void = (error: any) => {
    console.log(error);
  };

  const capture: (payload: ICapturePayload) => void = (
    payload: ICapturePayload
  ) => {
    setHasMotion(payload.hasMotion);
    setDataUrl(payload.hasMotion ? payload.getURL() : "");
  };
  const videoElement: HTMLVideoElement = document.createElement("video");
  const canvasElement: HTMLCanvasElement = document.createElement("canvas");
  diffCamEngine.init({
    video: videoElement,
    motionCanvas: canvasElement,
    initSuccessCallback: initSuccess,
    initErrorCallback: initError,
    captureCallback: capture,
    captureIntervalTime: 2000,
  });
  return (
    <div className="wrapper">
      {hasMotion ? <h1>Has motion</h1> : <h1>No motion detected</h1>}
      {/* <Title hasMotion={hasMotion}>Välkommen till</Title>
      <Logo
        src={dynabyteLogo}
        alt="logo"
        width="200"
        height="80"
        hasMotion={hasMotion}
      /> */}
      <span style={{ opacity: 0, position: "fixed" }}></span>
      <img src={dataUrl} alt="Bilden som skickas" />
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
