import React from 'react';
import '../App.css';
import dynabyteLogo from '../static/images/dynabyte_white.png';
import Logo from './Logo';

export const Title: React.FC = ({ children }) => {
  return (
    <>
      <h1
        style={{
          marginTop: 0,
          textAlign: 'center',
          fontSize: '4rem',
          color: 'white',
          opacity: 1,
        }}
      >
        {children}
      </h1>
      <Logo src={dynabyteLogo} alt='logo' width='200' height='80' />
    </>
  );
};

export default Title;
