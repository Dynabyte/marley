import React, { Dispatch, useState } from 'react';

const useModal = (): [
  boolean,
  Dispatch<React.SetStateAction<boolean>>,
  () => void
] => {
  const [isShowing, setIsShowing] = useState(false);

  const toggle = () => setIsShowing(!isShowing);

  return [isShowing, setIsShowing, toggle];
};

export default useModal;
