import { useState } from 'react';

const useModal = () => {
  const [isShowing, setIsShowing] = useState(false);

  const toggle = () => setIsShowing(!isShowing);

  return { isShowing, setIsShowing, toggle };
};

export default useModal;
