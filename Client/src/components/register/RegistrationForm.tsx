import React, {
  ChangeEvent,
  MouseEvent,
  SyntheticEvent,
  useContext,
  useState,
} from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import { RegistrationDataContext } from '../../contexts/RegistrationDataContext';
import Card from '../../ui/Card';
import CenterContent from '../../ui/CenterContent';
import Checkbox from '../../ui/Checkbox';
import LargeText from '../../ui/fonts/LargeText';
import SmallText from '../../ui/fonts/SmallText';
import Input from '../../ui/Input';
import PinkButton from '../../ui/PinkButton';
import { authorizeCalendar } from '../../utility/googleAuth';

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

const LabelText = styled.span`
  display: inline-block;
  font-size: 1.25rem;
  font-family: Arial, Helvetica, sans-serif;
  color: black;
  margin-bottom: 2rem;
`;

const RegistrationForm = () => {
  const { setRegistrationData } = useContext(RegistrationDataContext);
  const [checked, setIsChecked] = useState<boolean>(false);
  const [name, setName] = useState<string>('');
  const [errors, setErrors] = useState<string[]>([]);
  const history = useHistory();

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();
    setName(event.currentTarget.value);
  };

  const handleCancelButton = (event: MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    history.push('/');
  };

  const handleSubmit = (event: SyntheticEvent) => {
    event.preventDefault();

    const errors = validate(name);

    if (errors.length > 0) {
      setErrors(errors);
      return;
    }

    setErrors([]);

    if (checked) {
      authorizeCalendar((code) => {
        setRegistrationData({ name, authCode: code });
        history.push('/positioning');
      });
    } else {
      setRegistrationData({ name, authCode: null });
      history.push('/positioning');
    }
  };

  const validate = (name: string) => {
    let errors = [];
    if (name.trim().length === 0) {
      errors.push('Du måste fylla i ett namn');
    }

    if (name.length > 50) {
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
            value={name}
            onChange={handleChange}
            placeholder='För- och efternamn'
          />
          {errors && (
            <Errors>
              {errors.map((error, index) => (
                <p key={index}>{error}</p>
              ))}
            </Errors>
          )}
          <label>
            <LabelText>
              Vill du koppla din Google-kalender för att kunna se dina möten?
            </LabelText>
            <Checkbox
              checked={checked}
              onChange={() => setIsChecked(!checked)}
            />
          </label>

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
