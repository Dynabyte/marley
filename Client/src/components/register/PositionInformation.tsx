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

const Card = styled.div`
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

const Positioning = () => {
  const history = useHistory();
  const name = history.location.state;
  return (
    <Container>
      <Card>
      <h1>Positionering</h1>
      <ul>
        <li>Placera dig 1 meter ifrån skärmen.</li>
        <li>Titta mot kameran och var still i ca. 2 sekunder.</li>
        <li>Placera dig mitt framför skärmen.</li>
        </ul>
        <div style={{ display: 'flex', marginTop: '2rem' }}>
      <Button
            type="button"
            style={{ marginRight: '1rem' }}
            onClick={() => history.push('/')}
          >
            AVBRYT
          </Button>
      <Button
        onClick={() => history.push({ pathname: '/capture-frames', state: name })}
      >
        REDO
      </Button>
      </div>
      </Card>
    </Container>
  );
};

export default Positioning;
