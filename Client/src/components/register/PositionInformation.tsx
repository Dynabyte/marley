import React from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import Card from '../../ui/Card';
import CenterContent from '../../ui/CenterContent';
import LargeText from '../../ui/fonts/LargeText';
import PinkButton from '../../ui/PinkButton';

const StyledCard = styled(Card)`
  h1 {
    margin: 20px 0;
    color: #333;
  }
  li {
    color: #6e6d6d;
    font-size: 1.5rem;
    font-weight: 300;
    margin-bottom: 8px;
  }
`;

export interface registrationData {
  name: string;
  authCode: string;
}

const Positioning = () => {
  const history = useHistory();
  const location = useLocation<registrationData>();
  const { name, authCode } = location.state;
  return (
    <CenterContent>
      <StyledCard>
        <LargeText>Positionering</LargeText>
        <ul>
          <li>Placera dig ensam mitt framför skärmen.</li>
          <li>Titta mot kameran och var still i ca. 2 sekunder.</li>
        </ul>
        <div style={{ display: 'flex', marginTop: '2rem' }}>
          <PinkButton
            type='button'
            style={{ marginRight: '1rem' }}
            onClick={() => history.push('/')}
          >
            AVBRYT
          </PinkButton>
          <PinkButton
            type='button'
            onClick={() =>
              history.push({
                pathname: '/capture-frames',
                state: { name, authCode },
              })
            }
          >
            REDO
          </PinkButton>
        </div>
      </StyledCard>
    </CenterContent>
  );
};

export default Positioning;
