const calendarEventLogic = (eventResponse: any) => {
  let message: string = '';

  if (!eventResponse) {
    message += 'Du har inga kommande möten idag';
    return message;
  }

  const { event, hoursRemaining, minutesRemaining, isOngoing } = eventResponse;
  const eventTitle = event.summary;

  if(eventTitle === undefined){
    message = 'Ditt möte ';
  }
  else {
    message = `Ditt möte "${eventTitle}" `;
  }

  if (isOngoing) {
    const minutesPassed = minutesRemaining * -1;
    if (hoursRemaining === 0) {
      message += `började för ${minutesPassed} `;
      if (minutesPassed === 1) {
        message += 'minut sedan';
      } else {
        message += 'minuter sedan';
      }
    } else {
      message = ''; //Don't show event at all if it started more than an hour ago
    }
  } else {
    message += 'börjar om ';
    if (hoursRemaining > 0) {
      if (hoursRemaining === 1) {
        message += '1 timme och ';
      } else {
        message += `${hoursRemaining} timmar och `;
      }
    }
    if (minutesRemaining === 0) {
      message = `Ditt möte "${eventTitle}" börjar nu`;
    } else if (minutesRemaining === 1) {
      message += '1 minut';
    } else {
      message += `${minutesRemaining} minuter`;
    }
  }

  return message;
};

export default calendarEventLogic;
