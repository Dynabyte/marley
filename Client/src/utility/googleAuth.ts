import axios from 'axios';

export const authorizeCalendar = (
  onCompleteFunction: (code: string) => void
) => {
  axios
    .get('http://localhost:8080/calendar/credentials', {
      headers: { 'Content-Type': 'application/json' },
    })
    .then(({ data }) => {
      googleAuthRequest(data, onCompleteFunction);
    })
    .catch((error) => console.log(error));
};

const googleAuthRequest = (googleCredentials, onCompleteFunction) => {
  window.gapi.load('client:auth2', () => {
    window.gapi.client
      .init({
        apiKey: googleCredentials.googleCalendarApiKey,
        clientId: googleCredentials.clientId,
        scope: 'https://www.googleapis.com/auth/calendar.readonly',
      })
      .then(() => {
        window.gapi.auth2
          .getAuthInstance()
          .grantOfflineAccess()
          .then(({ code }) => {
            onCompleteFunction(code);
          });
      });
  });
};

export const saveGoogleCalendarTokens = (
  faceId: string,
  authCode: string,
  onCompleteFunction: () => void,
  onErrorFunction: () => void
) => {
  axios
    .post(
      'http://localhost:8080/calendar/tokens',
      { faceId, authCode },
      {
        headers: { 'Content-Type': 'application/json' },
      }
    )
    .then(() => {
      onCompleteFunction();
    })
    .catch((error) => {
      if (error.response) {
        const errorData = error.response.data;
        console.log(errorData);
      }
      onErrorFunction();
    });
};
