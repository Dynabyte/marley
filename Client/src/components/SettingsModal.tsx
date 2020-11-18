import { faWindowClose } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import React, { useState } from 'react';
import ReactDOM from 'react-dom';
import styled from 'styled-components';
import PinkButton from '../ui/PinkButton';
import {
  authorizeCalendar,
  saveGoogleCalendarTokens,
} from '../utility/googleAuth';
import DeleteConfirmationModal from './DeleteConfirmationModal';

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

const StyledButton = styled.button`
  position: absolute;
  top: 5px;
  right: 5px;
  background: none;
  border: none;
  outline: inherit;
`;

const ButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-around;
  height: 100%;
`;

const SettingsModal = ({
  isShowing,
  hide,
  faceId,
  hasAllowedCalendar,
  setPaused,
  setIsDeleting,
  setResult,
}) => {
  const [allowedCalendar, setAllowedCalendar] = useState<boolean>(
    hasAllowedCalendar
  );
  const [showConfirmationModal, setShowConfirmationModal] = useState<boolean>(
    false
  );
  const deleteUser = () => {
    hide();
    setIsDeleting(true);

    axios
      .delete(`http://localhost:8080/delete/${faceId}`, {
        headers: { 'Content-Type': 'application/json' },
      })
      .then(() => {
        console.log('Deleted from system!');
        setTimeout(() => {
          setIsDeleting(false);
          setPaused(false);
          setResult({});
        }, 1500);
      })
      .catch((error) => {
        setIsDeleting(false);
        setPaused(false);
        if (error.response) {
          const errorData = error.response.data;
          console.log(errorData);
        }
      });
  };

  const deleteCalendar = () => {
    axios
      .delete(`http://localhost:8080/calendar/tokens/${faceId}`, {
        headers: { 'Content-Type': 'application/json' },
      })
      .then(() => {
        setAllowedCalendar(false);
        console.log('removed calendar');
      })
      .catch((error) => {
        if (error.response) {
          const errorData = error.response.data;
          console.log(errorData);
        }
      });
  };

  const addCalendar = () => {
    authorizeCalendar((code) => {
      saveGoogleCalendarTokens(
        faceId,
        code,
        () => setAllowedCalendar(true),
        () => setAllowedCalendar(false)
      );
    });
  };

  if (isShowing) {
    return ReactDOM.createPortal(
      <>
        <ModalWrapper aria-modal aria-hidden tabIndex={-1} role='dialog'>
          <StyledModal>
            <ButtonContainer>
              <PinkButton
                onClick={() =>
                  allowedCalendar ? deleteCalendar() : addCalendar()
                }
              >
                {allowedCalendar
                  ? 'Ta bort kalender-notifikationer'
                  : 'Lägg till kalender-notifikationer'}
              </PinkButton>
              <PinkButton onClick={() => setShowConfirmationModal(true)}>
                Ta bort mig från systemet
              </PinkButton>
            </ButtonContainer>
            <StyledButton
              onClick={() => {
                hide();
                setResult({});
                setPaused(false);
              }}
            >
              <FontAwesomeIcon icon={faWindowClose} color='#910D18' size='3x' />
            </StyledButton>
          </StyledModal>
        </ModalWrapper>
        <DeleteConfirmationModal
          isShowing={showConfirmationModal}
          hide={() => setShowConfirmationModal(false)}
          deleteUser={deleteUser}
        />
      </>,
      document.body
    );
  } else return null;
};

export default SettingsModal;
