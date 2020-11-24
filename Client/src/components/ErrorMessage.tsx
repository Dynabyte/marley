import React, { FC, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import { ErrorMessageProps } from '../models/models';
import Card from '../ui/Card';
import CenterContent from '../ui/CenterContent';
import DefaultText from '../ui/fonts/DefaultText';
import PinkButton from '../ui/PinkButton';

const StyledCard = styled(Card)`
  span {
    display: inline-block;
    padding: 20px 0;
    color: #333;
  }
  text-align: center;
`;

const ErrorMessage: FC<ErrorMessageProps> = ({ message }) => {
  const history = useHistory();

  useEffect(() => {
    setTimeout(() => {
      history.push('/');
    }, 10000);
    return () => clearTimeout();
  }, [history]);

  return (
    <CenterContent>
      <StyledCard>
        <DefaultText>{message}</DefaultText>
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
