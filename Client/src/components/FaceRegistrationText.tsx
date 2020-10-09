import React, { useCallback } from 'react';
import { useHistory } from 'react-router-dom';
import Logo from '../shared/Logo';
import dynabyteLogo from '../static/images/dynabyte_white.png';



const FaceRegistrationText = () => {
  const history = useHistory();

  const handleClick = useCallback(() => {
    history.push('/registration');
  }, [history]);

  return (
  <>
    <h1>Välkommen till</h1>
    <Logo
        src={dynabyteLogo}
        alt='logo'
        width='200'
        height='80'
    />
  <p>Vi känner inte igen dig sen tidigare.</p>
  <p>Vill du registrera dig?</p>
  <button onClick={handleClick}>JA</button>
</>
);
}

export default FaceRegistrationText;