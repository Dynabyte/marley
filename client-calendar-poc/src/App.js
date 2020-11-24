import React, { useState } from 'react';
import './App.css';
import CalendarEvents from './CalendarEvents';
import logo from './logo.svg';

const App = () => {
  const [calendarEvents, setCalendarEvents] = useState([]);
  const faceId = '5f97ecb54d7fd812180ae5fa';

  const saveCalendarEvents = (events) => {
    let calendarEventList = [];
    events.map((event) =>
      calendarEventList.push(...calendarEvents, {
        start: new Date(event.start.dateTime.value).toUTCString(),
        attendees: event.attendees,
        location: event.location || 'Ej specificerat',
        summary: event.summary || 'No Title',
        description: event.description || 'No Description',
      })
    );
    setCalendarEvents(calendarEventList);
  };

  const handleClick = () => {
    fetch(`http://localhost:8080/calendar/${faceId}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.calendarEvents === null) {
          window.gapi.load('client:auth2', () => {
            window.gapi.client
              .init({
                apiKey: data.googleCredentials.googleCalendarApiKey,
                clientId: data.googleCredentials.clientId,
                scope: 'https://www.googleapis.com/auth/calendar.readonly',
              })
              .then(() => {
                window.gapi.auth2
                  .getAuthInstance()
                  .grantOfflineAccess()
                  .then(({ code }) => {
                    console.log(code);
                    fetch('http://localhost:8080/calendar/tokens', {
                      method: 'POST',
                      headers: { 'Content-Type': 'application/json' },
                      body: JSON.stringify({
                        faceId,
                        authCode: code,
                      }),
                    })
                      .then((res) => res.json())
                      .then((data) => {
                        if (data === 'OK') {
                          // Send request to get calendar after verified
                          fetch(`http://localhost:8080/calendar/${faceId}`, {
                            method: 'GET',
                            headers: { 'Content-Type': 'application/json' },
                          })
                            .then((res) => res.json())
                            .then((data) => {
                              saveCalendarEvents(data.calendarEvents);
                            });
                        }
                      });
                  })
                  .catch((error) => console.log(error));
              });
          });
        } else {
          saveCalendarEvents(data.calendarEvents);
        }
      })
      .catch((error) => {
        if (error.response) {
          console.log(error.response.data);
        }
      });
  };

  if (calendarEvents.length) {
    return (
      <div className='App'>
        <CalendarEvents events={calendarEvents} setEvents={setCalendarEvents} />
      </div>
    );
  }

  return (
    <div className='App'>
      <header className='App-header'>
        <img src={logo} className='App-logo' alt='logo' />

        <button onClick={handleClick}>See your calendar</button>
      </header>
    </div>
  );
};

export default App;
