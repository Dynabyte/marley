import React from 'react';
import '../App.css';

interface ILogo
  extends React.DetailedHTMLProps<
    React.ImgHTMLAttributes<HTMLImageElement>,
    HTMLImageElement
  > {
  hasMotion: boolean;
}

export const Logo: React.FC<ILogo> = (props) => {
  return (
    <img
      src={props.src}
      width={props.width}
      height={props.height}
      alt={props.alt}
      style={{
        order: props.hasMotion ? 2 : 1,
        marginTop: props.hasMotion ? 0 : '10vh',
        animation: 'all 2s',
      }}
    />
  );
};

export default Logo;
