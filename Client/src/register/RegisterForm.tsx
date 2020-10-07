import React, { ChangeEvent, useState } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100vh;
  align-items: center;
`;

const Input = styled.input`
  margin-bottom: 1rem;
`;

const RegisterForm = () => {
  const [value, setValue] = useState('');
  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();
    setValue(event.currentTarget.value);
  };
  const history = useHistory();
  return (
    <Container>
      <form>
        <h1>Registrering</h1>
        <h3>Skriv in ditt namn:</h3>
        <Input type='text' value={value} onChange={handleChange} />
        <div style={{ textAlign: 'center' }}>
          <button
            style={{ marginRight: '1rem' }}
            onClick={() => history.push('/')}
          >
            AVBRYT
          </button>
          <button
            onClick={() =>
              history.push({ pathname: '/positioning', state: value })
            }
          >
            NÄSTA
          </button>
        </div>
      </form>
    </Container>
  );
};

export default RegisterForm;
