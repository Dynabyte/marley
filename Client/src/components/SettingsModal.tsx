import React from 'react';
import ReactDOM from 'react-dom';
import styled from 'styled-components';
import PinkButton from '../ui/PinkButton';

const ModalWrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1050;
  width: 100%;
  height: 100%;
  color: black;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
`;

const StyledModal = styled.div`
  z-index: 100;
  background: white;
  position: relative;
  border-radius: 3px;
  width: 550px;
  height: 300px;
  padding: 2rem;
`;

const StyledPinkButton = styled(PinkButton)`
  position: absolute;
  bottom: 5px;
`;

const SettingsModal = ({
  isShowing,
  hide,
  handleClick,
  children,
  faceId,
  hasAllowedCalendar,
}) => {
  const onClick = () => {
    handleClick();
    hide();
  };

  const deleteCalendar = () => {};

  const addCalendar = () => {};

  if (isShowing) {
    return ReactDOM.createPortal(
      <>
        <ModalWrapper aria-modal aria-hidden tabIndex={-1} role='dialog'>
          <StyledModal>
            <PinkButton
              onClick={() =>
                hasAllowedCalendar ? deleteCalendar : addCalendar
              }
            >
              {hasAllowedCalendar
                ? 'Ta bort kalender-notifikationer'
                : 'Lägg till kalender-notifikationer'}
            </PinkButton>
            <StyledPinkButton onClick={onClick}>Stäng</StyledPinkButton>
          </StyledModal>
        </ModalWrapper>
      </>,
      document.body
    );
  } else return null;
};

export default SettingsModal;
