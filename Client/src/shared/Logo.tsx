import React from 'react';

interface ILogo
  extends React.DetailedHTMLProps<
    React.ImgHTMLAttributes<HTMLImageElement>,
    HTMLImageElement
  > {
  isKnownFace: boolean;
}

export const Logo: React.FC<ILogo> = (props) => {
  return (
    <img
      src={props.src}
      width={props.width}
      height={props.height}
      alt={props.alt}
      style={{
        order: props.isKnownFace ? 2 : 1,
        marginTop: props.isKnownFace ? 0 : '10vh',
        animation: 'all 2s',
      }}
    />
  );
};

export default Logo;
