import styled, { keyframes } from 'styled-components';

const spin = keyframes`
  0% { transform: rotate(0deg)}
  100% { transform: rotate(360deg)}
`;

const Spinner = styled.div`
  border: 10px solid rgba(255, 255, 255, 0.5);
  border-top: 10px solid white;
  border-radius: 50%;
  width: 80px;
  height: 80px;
  animation: 2s linear infinite ${spin};
  margin-top: 20px;
`;

export default Spinner;
