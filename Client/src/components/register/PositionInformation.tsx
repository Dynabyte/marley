import React from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import Card from '../../ui/Card';
import CenterContent from '../../ui/CenterContent';
import PinkButton from '../../ui/PinkButton';

const StyledCard = styled(Card)`
  h1 {
    margin: 20px 0;
    color: #333;
    font-size: 2rem;
    font-weight: bold;
  }
  li {
    color: #6e6d6d;
    font-size: 1.5rem;
    font-weight: 300;
    margin-bottom: 8px;
  }
`;

const Positioning = () => {
  const history = useHistory();
  const name = history.location.state;
  return (
    <CenterContent>
      <StyledCard>
        <h1>Positionering</h1>
        <ul>
          <li>Placera dig 1 meter ifrån skärmen.</li>
          <li>Titta mot kameran och var still i ca. 2 sekunder.</li>
          <li>Placera dig mitt framför skärmen.</li>
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
              history.push({ pathname: '/capture-frames', state: name })
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
