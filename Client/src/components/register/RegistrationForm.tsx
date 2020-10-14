import React, {
  ChangeEvent,
  MouseEvent,
  SyntheticEvent,
  useState,
} from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import Card from '../../ui/Card';
import CenterContent from '../../ui/CenterContent';
import Input from '../../ui/Input';
import PinkButton from '../../ui/PinkButton';

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
  };

  const handleSubmit = (event: SyntheticEvent) => {
    event.preventDefault();

    const errors = validate(value);

    if (errors.length > 0) {
      setErrors(errors);
      return;
    }

    setErrors([]);
    history.push({ pathname: '/positioning', state: value });
  };

  const validate = (value: string) => {
    let errors = [];
    if (value.trim().length === 0) {
      errors.push('Du måste fylla i ett namn');
    }

    if (value.length > 50) {
      errors.push('Det får inte vara fler än 50 tecken');
    }
    return errors;
  };

  return (
    <CenterContent>
      <Card>
        <form onSubmit={handleSubmit}>
          <Header>
            <h1>Registrering</h1>
            <p>Fyll i dina uppgifter</p>
          </Header>
          <Seperator />
          <Input
            type='text'
            value={value}
            onChange={handleChange}
            placeholder='För- och efternamn'
          />
          <Errors style={{ color: 'black' }}>
            {errors.map((error, index) => (
              <p key={index}>{error}</p>
            ))}
          </Errors>
          <div style={{ display: 'flex' }}>
            <PinkButton
              type='button'
              style={{ marginRight: '1rem' }}
              onClick={handleCancelButton}
            >
              AVBRYT
            </PinkButton>
            <PinkButton type='submit'>NÄSTA</PinkButton>
          </div>
        </form>
      </Card>
    </CenterContent>
  );
};

export default RegistrationForm;
