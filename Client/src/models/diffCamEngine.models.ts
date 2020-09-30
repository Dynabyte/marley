export interface ICapturePayload {
  imageData: ImageData | null;
  score: number;
  hasMotion: boolean;
  motionBox?: IMotionBox;
  motionPixels: any;
  getURL: () => string;
  checkMotionPixel: (x: number, y: number) => boolean;
}

export interface IMotionBox {
  x: IMinMax;
  y: IMinMax;
}

export interface IMinMax {
  min: number;
  max: number;
}

export interface ICoordinates {
  x: number;
  y: number;
}

export interface IDiffCamEngine {
  getPixelDiffThreshold: () => number;
  setPixelDiffThreshold: (val: number) => void;
  getScoreThreshold: () => number;
  setScoreThreshold: (val: number) => void;
  init: (options: IDiffCamEngineOptions) => void;
  start: () => void;
  stop: () => void;
}

export interface IDiffCamEngineOptions {
  video?: HTMLVideoElement;
  motionCanvas?: HTMLCanvasElement;
  captureIntervalTime?: number;
  captureWidth?: number;
  captureHeight?: number;
  diffWidth?: number;
  diffHeight?: number;
  pixelDiffThreshold?: number;
  scoreThreshold?: number;
  includeMotionBox?: boolean;
  includeMotionPixels?: boolean;
  initSuccessCallback?: () => void;
  initErrorCallback?: (error: any) => void;
  startCompleteCallback?: () => void;
  captureCallback?: (payload: ICapturePayload) => void;
}
