import React, { ChangeEvent, MouseEvent, SyntheticEvent, useState } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100vh;
  align-items: center;
`;

const Form = styled.form`
  padding: 0px 25px 25px;
  background: #fff;
  box-shadow: 
        0px 0px 0px 5px rgba( 255,255,255,0.4 ), 
        0px 4px 20px rgba( 0,0,0,0.33 );
    border-radius: 5px;
    width: 40rem;

    @media screen and (max-width: 575.98px) {
      width: 95%;
    }
`;

const Header = styled.div`
  margin: 20px 0;
  
  h1 {
    color: #333;
    font-size: 2.5rem;
    font-weight: bold;
    margin-bottom: 5px;
  }

  p {
    color: #6e6d6d;
    font-size: 1.5rem;
    font-weight: 300;
  }
`;

const Input = styled.input`
  margin: 25px 0;
  background: #f5f5f5;
    font-size: 1.25rem;
    border-radius: 3px;
    border: none;
    padding: 13px 10px;
    width: 100%;
    box-shadow: inset 1px 2px 3px rgba(0,0,0,0.2);
`;

const Button = styled.button`
  width: 50%;
    padding: 15px 0;
    color: #fff;
    font-size: 1rem;
    font-weight: 500;
    letter-spacing: 1px;
    text-align: center;
    text-decoration: none;
    background: -webkit-gradient(
        linear, left top, left bottom, 
        from(#cba4c9),
        to(#e3b6e1));
    border-radius: 5px;
    border: 1px solid #737b8d;

    :hover {
    background: -webkit-gradient(
        linear, left top, left bottom, 
        from(#e3b6e1),
        to(#cba4c9));
    }
`;

const Seperator = styled.div`
    height: 1px;
    background: #e8e8e8;
    margin: 0px -25px;

`;

const Errors = styled.div`
  margin: -15px 0 25px 0;
  font-weight: bold;
  font-size: 1.25rem;
`;


const RegistrationForm = () => {
  const [value, setValue] = useState('');
  const [errors, setErrors] = useState([]);
  const history = useHistory();

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();
    setValue(event.currentTarget.value);
  };

  const handleCancelButton = (event: MouseEvent<HTMLButtonElement>) => {
    event.preventDefault(); 
    history.push('/');
  }

  const handleSubmit = (event: SyntheticEvent) => {
    event.preventDefault(); 
    
    const errors = validate(value);

    if(errors.length > 0) {
      setErrors(errors);
      return;
    }

    setErrors([]);
    history.push({ pathname: '/positioning', state: value });
  }

  const validate = (value: string) => {
    let errors = [];
    if(value.length === 0) {
      errors.push('Name is required');
    }
    if(value.trim().length > 50) {
      errors.push('Must be less than 50 characters');
    }
    return errors;
  };

  
  return (
    <Container>
      <Form onSubmit={handleSubmit}>
        <Header>
        <h1>Registrering</h1>
        <p>Fyll i dina uppgifter</p>
        </Header>
        <Seperator />
        <Input type='text' value={value} onChange={handleChange} placeholder='För- och efternamn' />
        <Errors style={{ color: 'black'}}>{errors.map((error,index) => <p key={index}>{error}</p>)}</Errors>
        <div style={{ display: 'flex' }}>
          <Button
            type="button"
            style={{ marginRight: '1rem' }}
            onClick={handleCancelButton}
          >
            AVBRYT
          </Button>
          <Button
            type="submit"
          >
            NÄSTA
          </Button>
        </div>
      </Form>
    </Container>
  );
};

export default RegistrationForm;
