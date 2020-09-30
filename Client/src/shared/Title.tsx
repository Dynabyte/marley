import React from 'react';


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
        animation: `2s ease-in-out 0s 1`,
        order: hasMotion ? 1 : 2,
        opacity: hasMotion ? 1 : 0,
      }}
      >{children}</span>
  );
};

export default Title;
