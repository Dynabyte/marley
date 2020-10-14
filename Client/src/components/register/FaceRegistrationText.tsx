import React, { useCallback } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import Title from '../../shared/Title';
import WhiteButton from '../../ui/WhiteButton';

const Register = styled.div`
  font-size: 3rem;
  font-weight: bold;

  p {
    margin: 10px;
  }
`;

const Row = styled.div`
  display: flex;
`;

const FaceRegistrationText = () => {
  const history = useHistory();

  const handleClick = useCallback(() => {
    history.push('/registration');
  }, [history]);

  return (
    <>
      <Title>Välkommen till</Title>
      <Register>
        <p>Vi känner inte igen dig sen tidigare.</p>
        <Row>
          <p>Vill du registrera dig?</p>
          <WhiteButton onClick={handleClick}>REGISTRERA DIG NU</WhiteButton>
        </Row>
      </Register>
    </>
  );
};

export default FaceRegistrationText;
