import {
  detectSingleFace,
  loadSsdMobilenetv1Model,
  SsdMobilenetv1Options,
} from "face-api.js";
import React from "react";
import "./App.css";
import dynabyteLogo from "./dynabyte_white.png";

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
      <h1
        id="title"
        style={{
          fontSize: "70px",
          visibility: isDetected ? "visible" : "hidden",
        }}
      >
        Välkommen till
      </h1>
      <img src={dynabyteLogo} alt="logo" width="200" height="80" />
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
