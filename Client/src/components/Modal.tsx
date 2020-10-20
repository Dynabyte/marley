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
  height: 200px;
  padding: 2rem;
`;

const Text = styled.p`
  margin-bottom: 3rem;
  text-align: center;
  font-size: 1.5rem;
  font-weight: 300;
`;

const Modal = ({ isShowing, hide, handleClick, setPaused }) => {
  const onClick = () => {
    handleClick();
    hide();
  };

  if (isShowing) {
    return ReactDOM.createPortal(
      <React.Fragment>
        <ModalWrapper aria-modal aria-hidden tabIndex={-1} role='dialog'>
          <StyledModal>
            <Text>Vill du verkligen ta bort dig från systemet?</Text>
            <div style={{ display: 'flex' }}>
              <PinkButton
                type='button'
                data-dismiss='modal'
                aria-label='Close'
                onClick={onClick}
                style={{ marginRight: '1rem' }}
              >
                <span aria-hidden='true'>JA</span>
              </PinkButton>
              <PinkButton
                type='button'
                data-dismiss='modal'
                aria-label='Close'
                onClick={() => {
                  setPaused(false);
                  hide();
                }}
              >
                <span aria-hidden='true'>NEJ</span>
              </PinkButton>
            </div>
          </StyledModal>
        </ModalWrapper>
      </React.Fragment>,
      document.body
    );
  } else return null;
};

export default Modal;
