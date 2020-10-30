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
import LargeText from '../../ui/fonts/LargeText';
import SmallText from '../../ui/fonts/SmallText';
import Input from '../../ui/Input';
import PinkButton from '../../ui/PinkButton';

const Header = styled.div`
  margin: 20px 0;
  h1 {
    color: #333;
    margin-bottom: 5px;
  }
  span {
    color: #6e6d6d;
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
  color: black;
`;

const RegistrationForm = () => {
  const [value, setValue] = useState<string>('');
  const [errors, setErrors] = useState<string[]>([]);
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
            <LargeText>Registrering</LargeText>
            <SmallText>Fyll i dina uppgifter</SmallText>
          </Header>
          <Seperator />
          <Input
            type='text'
            value={value}
            onChange={handleChange}
            placeholder='För- och efternamn'
          />
          <Errors>
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
