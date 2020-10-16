import React from 'react';
import styled, { css, keyframes } from 'styled-components';
import '../App.css';

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
  ${(props: ITitle) => props.isKnownFace && animation};
`;

interface ITitle {
  isKnownFace: boolean;
}

export const Title: React.FC<ITitle> = ({ isKnownFace, children }) => {
  return (
    <StyledTitle
      isKnownFace={isKnownFace}
      style={{
        fontFamily: "'Playfair Display', serif",
        marginTop: 0,
        textAlign: 'center',
        fontSize: '4rem',
        color: 'white',
        animation: `2s ease-in-out 0s 1`,
        order: isKnownFace ? 1 : 2,
        opacity: isKnownFace ? 1 : 0,
      }}
    >
      {children}
    </StyledTitle>
  );
};

export default Title;
