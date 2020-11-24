import React from 'react';
import { ILogo } from '../models/models';

export const Logo: React.FC<ILogo> = (props) => {
  return (
    <>
      <img
        src={props.src}
        width={props.width}
        height={props.height}
        alt={props.alt}
        style={{
          marginBottom: '2rem',
          animation: 'all 2s',
        }}
      />
    </>
  );
};

export default Logo;
