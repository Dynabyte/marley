import React from 'react';
import {
    keyframes
} from 'styled-components';
import './App.css';

//TODO: Use these styling when we are ready.
const slideIn = keyframes`
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
`;

interface ITitle {
  hasMotion: boolean;
}

export const Title: React.FC<ITitle> = ({ hasMotion, children }) => {
  return (
    <span
      style={{
        fontFamily: "'Playfair Display', serif",
        marginTop: 0,
        textAlign: 'center',
        fontSize: '4rem',
        color: 'white',
        animation: `2s  ease-in-out 0s 1 ${slideIn}`,
        order: hasMotion ? 1 : 2,
        opacity: hasMotion ? 1 : 0,
      }}
      >{children}</span>
  );
};

export default Title;
