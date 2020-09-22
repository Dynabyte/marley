//ÄNDRA INGET HÄR UTAN ATT PRATA IHOP MED ANDRA

export const DiffCamEngine: () => IDiffCamEngine = () => {
  let stream: any; // stream obtained from webcam
  let video: any; // shows stream
  let captureCanvas: any; // internal canvas for capturing full images from video
  let captureContext: any; // context for capture canvas
  let diffCanvas: any; // internal canvas for diffing downscaled captures
  let diffContext: any; // context for diff canvas
  let motionCanvas: any; // receives processed diff images
  let motionContext: any; // context for motion canvas

  let initSuccessCallback: any; // called when init succeeds
  let initErrorCallback: (error: any) => void; // called when init fails
  let startCompleteCallback: any; // called when start is complete
  let captureCallback: any; // called when an image has been captured and diffed

  let captureInterval: any; // interval for continuous captures
  let captureIntervalTime: any; // time between captures, in ms
  let captureWidth: any; // full captured image width
  let captureHeight: any; // full captured image height
  let diffWidth: any; // downscaled width for diff/motion
  let diffHeight: any; // downscaled height for diff/motion
  let isReadyToDiff: any; // has a previous capture been made to diff against?
  let pixelDiffThreshold: any; // min for a pixel to be considered significant
  let scoreThreshold: any; // min for an image to be considered significant
  let includeMotionBox: any; // flag to calculate and draw motion bounding box
  let includeMotionPixels: any; // flag to create object denoting pixels with motion

  let coords: { x: number; y: number };

  const init = (options: any) => {
    // sanity check
    if (!options) {
      throw new Error('No options object provided');
    }

    // incoming options with defaults
    video = options.video || document.createElement('video');
    motionCanvas = options.motionCanvas || document.createElement('canvas');
    captureIntervalTime = options.captureIntervalTime || 100;
    captureWidth = options.captureWidth || 640;
    captureHeight = options.captureHeight || 480;
    diffWidth = options.diffWidth || 64;
    diffHeight = options.diffHeight || 48;
    pixelDiffThreshold = options.pixelDiffThreshold || 32;
    scoreThreshold = options.scoreThreshold || 16;
    includeMotionBox = options.includeMotionBox || false;
    includeMotionPixels = options.includeMotionPixels || false;

    // callbacks
    initSuccessCallback = options.initSuccessCallback || function () {};
    initErrorCallback = options.initErrorCallback || function () {};
    startCompleteCallback = options.startCompleteCallback || function () {};
    captureCallback = options.captureCallback || function () {};

    // non-configurable
    captureCanvas = document.createElement('canvas');
    diffCanvas = document.createElement('canvas');
    isReadyToDiff = false;

    // prep video
    video.autoplay = true;

    // prep capture canvas
    captureCanvas.width = captureWidth;
    captureCanvas.height = captureHeight;
    captureContext = captureCanvas.getContext('2d');

    // prep diff canvas
    diffCanvas.width = diffWidth;
    diffCanvas.height = diffHeight;
    diffContext = diffCanvas.getContext('2d');

    // prep motion canvas
    motionCanvas.width = diffWidth;
    motionCanvas.height = diffHeight;
    motionContext = motionCanvas.getContext('2d');

    requestWebcam();
  };

  const requestWebcam = () => {
    navigator.mediaDevices
      .getUserMedia({
        audio: false,
        video: {
          width: { ideal: captureWidth },
          height: { ideal: captureHeight },
        },
      })
      .then(initSuccess)
      .catch(initError);
  };

  const initSuccess = (requestedStream: any) => {
    console.log(requestedStream);
    stream = requestedStream;
    initSuccessCallback();
  };

  const initError = (error: any) => {
    console.log(error);
    initErrorCallback(error);
  };

  const start = () => {
    if (!stream) {
      throw new Error('Cannot start after init fail');
    }

    // streaming takes a moment to start
    video.addEventListener('canplay', startComplete);
    video.srcObject = stream;
  };

  const startComplete = () => {
    video.removeEventListener('canplay', startComplete);
    captureInterval = setInterval(capture, captureIntervalTime);
    startCompleteCallback();
  };

  const stop = () => {
    clearInterval(captureInterval);
    video.src = '';
    motionContext.clearRect(0, 0, diffWidth, diffHeight);
    isReadyToDiff = false;
  };

  const capture = () => {
    // save a full-sized copy of capture
    captureContext.drawImage(video, 0, 0, captureWidth, captureHeight);
    let captureImageData = captureContext.getImageData(
      0,
      0,
      captureWidth,
      captureHeight
    );

    // diff current capture over previous capture, leftover from last time
    diffContext.globalCompositeOperation = 'difference';
    diffContext.drawImage(video, 0, 0, diffWidth, diffHeight);
    let diffImageData = diffContext.getImageData(0, 0, diffWidth, diffHeight);

    if (isReadyToDiff) {
      let diff = processDiff(diffImageData);

      motionContext.putImageData(diffImageData, 0, 0);
      if (diff.motionBox) {
        motionContext.strokeStyle = '#fff';
        motionContext.strokeRect(
          diff.motionBox.x.min + 0.5,
          diff.motionBox.y.min + 0.5,
          diff.motionBox.x.max - diff.motionBox.x.min,
          diff.motionBox.y.max - diff.motionBox.y.min
        );
      }
      captureCallback({
        imageData: captureImageData,
        score: diff.score,
        hasMotion: diff.score >= scoreThreshold,
        motionBox: diff.motionBox,
        motionPixels: diff.motionPixels,
        getURL: function () {
          return getCaptureUrl(this.imageData);
        },
        checkMotionPixel: function (x: any, y: any) {
          return checkMotionPixel(this.motionPixels, x, y);
        },
      });
    }

    // draw current capture normally over diff, ready for next time
    diffContext.globalCompositeOperation = 'source-over';
    diffContext.drawImage(video, 0, 0, diffWidth, diffHeight);
    isReadyToDiff = true;
  };

  const processDiff = (diffImageData: any) => {
    let rgba = diffImageData.data;

    // pixel adjustments are done by reference directly on diffImageData
    let score = 0;
    let motionPixels = includeMotionPixels ? [] : undefined;
    let motionBox = undefined;
    for (let i = 0; i < rgba.length; i += 4) {
      let pixelDiff = rgba[i] * 0.3 + rgba[i + 1] * 0.6 + rgba[i + 2] * 0.1;
      let normalized = Math.min(255, pixelDiff * (255 / pixelDiffThreshold));
      rgba[i] = 0;
      rgba[i + 1] = normalized;
      rgba[i + 2] = 0;

      if (pixelDiff >= pixelDiffThreshold) {
        score++;
        coords = calculateCoordinates(i / 4);

        if (includeMotionBox) {
          motionBox = calculateMotionBox(motionBox, coords.x, coords.y);
        }

        if (includeMotionPixels) {
          motionPixels = calculateMotionPixels(
            motionPixels,
            coords.x,
            coords.y,
            pixelDiff
          );
        }
      }
    }

    return {
      score: score,
      motionBox: score > scoreThreshold ? motionBox : undefined,
      motionPixels: motionPixels,
    };
  };

  const calculateCoordinates = (pixelIndex: any) => {
    return {
      x: pixelIndex % diffWidth,
      y: Math.floor(pixelIndex / diffWidth),
    };
  };

  const calculateMotionBox = (currentMotionBox: any, x: any, y: any) => {
    // init motion box on demand
    let motionBox = currentMotionBox || {
      x: { min: coords.x, max: x },
      y: { min: coords.y, max: y },
    };

    motionBox.x.min = Math.min(motionBox.x.min, x);
    motionBox.x.max = Math.max(motionBox.x.max, x);
    motionBox.y.min = Math.min(motionBox.y.min, y);
    motionBox.y.max = Math.max(motionBox.y.max, y);

    return motionBox;
  };

  const calculateMotionPixels = (
    motionPixels: any,
    x: number,
    y: number,
    pixelDiff: any
  ) => {
    motionPixels[x] = motionPixels[x] || [];
    motionPixels[x][y] = true;

    return motionPixels;
  };

  const getCaptureUrl = (captureImageData: any) => {
    // may as well borrow captureCanvas
    captureContext.putImageData(captureImageData, 0, 0);
    return captureCanvas.toDataURL();
  };

  const checkMotionPixel = (motionPixels: any, x: number, y: number) => {
    return motionPixels && motionPixels[x] && motionPixels[x][y];
  };

  const getPixelDiffThreshold = () => {
    return pixelDiffThreshold;
  };

  const setPixelDiffThreshold = (val: any) => {
    pixelDiffThreshold = val;
  };

  const getScoreThreshold = () => {
    return scoreThreshold;
  };

  const setScoreThreshold = (val: any) => {
    scoreThreshold = val;
  };

  return {
    // public getters/setters
    getPixelDiffThreshold: getPixelDiffThreshold,
    setPixelDiffThreshold: setPixelDiffThreshold,
    getScoreThreshold: getScoreThreshold,
    setScoreThreshold: setScoreThreshold,

    // public functions
    init: init,
    start: start,
    stop: stop,
  };
};

export interface ICapturePayload {
  imageData: any;
  score: number;
  hasMotion: boolean;
  motionBox: any;
  motionPixels: any;
  getURL: () => string;
  checkMotionPixel: (x: any, y: any) => any;
}

export interface IDiffCamEngine {
  getPixelDiffThreshold: () => any;
  setPixelDiffThreshold: (val: any) => void;
  getScoreThreshold: () => any;
  setScoreThreshold: (val: any) => void;
  init: (options: any) => void;
  start: () => void;
  stop: () => void;
}