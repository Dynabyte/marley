import React from 'react';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100vh;
  align-items: center;
`;

const Positioning = () => {
  const history = useHistory();
  const name = history.location.state;
  return (
    <Container>
      <h1>Positionering</h1>
      <ul>
        <li>Placera dig 1 meter ifrån skärmen.</li>
        <li>Titta mot kameran och var still i ca. 2 sekunder.</li>
        <li>Placera dig mitt framför skärmen.</li>
      </ul>
      <button
        onClick={() => history.push({ pathname: '/capture-frames', state: name })}
      >
        REDO
      </button>
    </Container>
  );
};

export default Positioning;
