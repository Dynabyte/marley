import React, { useCallback } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import Title from '../../shared/Title';
import LargeText from '../../ui/fonts/LargeText';
import WhiteButton from '../../ui/WhiteButton';

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
      <LargeText>Vi känner inte igen dig sen tidigare.</LargeText>
      <Row>
        <LargeText>Vill du registrera dig?</LargeText>
        <WhiteButton onClick={handleClick}>REGISTRERA DIG NU</WhiteButton>
      </Row>
    </>
  );
};

export default FaceRegistrationText;
