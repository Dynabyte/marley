import React, { useEffect } from 'react';
import './App.css';
import logo from './logo.svg';

function App() {
  useEffect(() => {
    console.log(process.env.REACT_APP_GOOGLE_CALENDAR_API_KEY);
    window.gapi.load('client:auth2', () => {
      window.gapi.client.init({
        apiKey: process.env.REACT_APP_GOOGLE_CALENDAR_API_KEY,
        clientId: process.env.REACT_APP_GOOGLE_CALENDAR_CLIENT_ID,
        scope: 'https://www.googleapis.com/auth/calendar.readonly',
        prompt: 'none',
      });
    });
  }, []);

  const handleClick = () => {
    fetch('http://localhost:8000/calendar/98767', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    })
      .then((res) => {
        console.log('person has an access token');

        if (res.status === 401) {
          console.log('Person has no access token');
          window.gapi.auth2
            .getAuthInstance()
            .grantOfflineAccess()
            .then((res) => {
              console.log(res);
            })
            .catch((error) => console.log(error));
        }
      })
      .catch((error) => {
        console.log('error');
        if (error.response) {
          console.log(error.response.data);
        }
      });
  };

  return (
    <div className='App'>
      <header className='App-header'>
        <img src={logo} className='App-logo' alt='logo' />

        <button onClick={handleClick}>See your calendar</button>
      </header>
    </div>
  );
}

export default App;
