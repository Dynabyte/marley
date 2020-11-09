import React from 'react';

const CalendarEvents = ({ events, setEvents }) => {
  const handleClick = () => {
    setEvents([]);
  };

  return (
    <div>
      {events.map((event, index) => {
        return (
          <div key={index}>
            <h2>{`Ditt möte startar ${event.start}`}</h2>
            <p>{`Sammanfattning: ${event.summary}`}</p>
            <p>{`Mötesrum: ${event.location}`}</p>
            <p>{`Beskrivning: ${event.description}`}</p>
            <div>
              {event.attendees.map((attendee, index) => {
                return (
                  <div key={index}>{`Deltagare: ${attendee.displayName}`}</div>
                );
              })}
            </div>
          </div>
        );
      })}
      <button onClick={handleClick}>Klar. Gå tillbaka till startsidan </button>
    </div>
  );
};

export default CalendarEvents;
