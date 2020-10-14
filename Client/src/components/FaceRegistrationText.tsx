import React, { useCallback } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import Title from '../shared/Title';

const Button = styled.button`
    padding: 15px;
    margin: 0 10px;
    letter-spacing: 1px;
    border-radius: 5px;
    border: 1px solid #737b8d;
    font-size: 1rem;
    font-weight: 500;
    opacity: 0.8;

  `;

 const Register = styled.div`
 font-size: 3rem;
 font-weight: bold;

 p {
   margin: 10px;
 }
 `;

 const Text = styled.div`
  display: flex;
  `



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
  <Text>
  <p>Vill du registrera dig?</p>
  <Button onClick={handleClick}>REGISTRERA DIG NU</Button>
  </Text>
  </Register>
</>
);
}

export default FaceRegistrationText;