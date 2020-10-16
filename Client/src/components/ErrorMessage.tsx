import React, {useEffect} from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import Card from '../ui/Card';
import CenterContent from '../ui/CenterContent';
import PinkButton from '../ui/PinkButton';

const StyledCard = styled(Card)`
  h1 {
    margin: 20px 0;
    color: #333;
    font-size: 2rem;
    font-weight: bold;
  }
  text-align: center;
`;

const ErrorMessage = () => {
  const history = useHistory();
  const message = history.location.state;

  useEffect(() => {
      setTimeout(() => {
          history.push('/');
      }, 10000);
      return () => clearTimeout();
  }, [history]);

  return (
    <CenterContent>
      <StyledCard>
        <h1>{message}</h1>
          <PinkButton
            type='button'
            style={{ marginRight: '1rem' }}
            onClick={() => history.push('/')}
          >
            OK
          </PinkButton>
      </StyledCard>
    </CenterContent>
  );
};

export default ErrorMessage;
