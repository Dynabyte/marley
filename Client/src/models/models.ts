export interface IPredictionResult {
  isKnownFace?: boolean;
  isFace?: boolean;
  name?: string;
  id?: string;
  hasAllowedCalendar?: boolean;
}

export interface IDeleteConfirmationModal {
  isShowing: boolean;
  hide: () => void;
  deleteUser: () => void;
}

export interface ErrorMessageProps {
  message: string;
}

export interface ISettingsModal {
  isShowing: boolean;
  hide: () => void;
  faceId: string;
  hasAllowedCalendar: boolean;
  setPaused: (item: boolean) => void;
  setIsDeleting: (item: boolean) => void;
  setResult: (result: IPredictionResult) => void;
}

export interface IErrorData {
  hasError: boolean;
  errorMessage: string;
}

export interface IRegistrationData {
  name: string;
  authCode: string;
}

export interface ILogo
  extends React.DetailedHTMLProps<
    React.ImgHTMLAttributes<HTMLImageElement>,
    HTMLImageElement
  > {}

export interface ICheckboxProps {
  checked: boolean;
}

export interface IGoogleCredentials {
  googleCalendarApiKey: string;
  clientId: string;
}

export interface IEventResponse {
  event: gapi.client.calendar.Event;
  hoursRemaining: number;
  minutesRemaining: number;
  isOngoing: boolean;
}
