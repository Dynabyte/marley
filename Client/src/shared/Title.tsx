import React from 'react';
import styled, { css, keyframes } from 'styled-components';
import '../App.css';
import dynabyteLogo from '../static/images/dynabyte_white.png';
import Logo from './Logo';

const slideIn = keyframes`
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
`;

const animation = css`
  animation: 2s ease-in-out 0s 1 ${slideIn};
`;

const StyledTitle = styled.h1`
  ${(props: ITitle) => props.hasMotion && animation};
`;

interface ITitle {
  hasMotion: boolean;
}

export const Title: React.FC<ITitle> = ({ hasMotion, children }) => {
  return (
    <>
    <StyledTitle
      hasMotion={hasMotion}
      style={{
        fontFamily: "'Playfair Display', serif",
        marginTop: 0,
        textAlign: 'center',
        fontSize: '4rem',
        color: 'white',
        animation: `2s ease-in-out 0s 1`,
        opacity: hasMotion ? 1 : 0,
      }}
    >
      {children}
      
    
    </StyledTitle>
    <Logo
    src={dynabyteLogo}
    alt='logo'
    width='200'
    height='80'
  />
  </>
  );
};

export default Title;
