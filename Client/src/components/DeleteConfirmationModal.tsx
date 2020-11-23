import React from 'react';
import ReactDOM from 'react-dom';
import styled from 'styled-components';
import { IDeleteConfirmationModal } from '../models/models';
import SmallText from '../ui/fonts/SmallText';
import PinkButton from '../ui/PinkButton';

const ModalWrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1200;
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

const StyledSmallText = styled(SmallText)`
  display: inline-block;
  margin-bottom: 3rem;
  text-align: center;
`;

const DeleteConfirmationModal = ({
  isShowing,
  hide,
  deleteUser,
}: IDeleteConfirmationModal) => {
  const onClick = () => {
    deleteUser();
    hide();
  };

  if (isShowing) {
    return ReactDOM.createPortal(
      <>
        <ModalWrapper aria-modal aria-hidden tabIndex={-1} role='dialog'>
          <StyledModal>
            <StyledSmallText>
              Vill du verkligen ta bort dig från systemet?
            </StyledSmallText>
            <div style={{ display: 'flex' }}>
              <PinkButton
                type='button'
                aria-label='Yes'
                onClick={onClick}
                style={{ marginRight: '1rem' }}
              >
                <span aria-hidden='true'>JA</span>
              </PinkButton>
              <PinkButton
                type='button'
                aria-label='No'
                onClick={() => {
                  hide();
                }}
              >
                <span aria-hidden='true'>NEJ</span>
              </PinkButton>
            </div>
          </StyledModal>
        </ModalWrapper>
      </>,
      document.body
    );
  } else return null;
};

export default DeleteConfirmationModal;
