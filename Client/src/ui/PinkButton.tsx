import styled from 'styled-components';

const PinkButton = styled.button`
  width: 50%;
  padding: 15px 0;
  color: #fff;
  font-size: 1rem;
  font-weight: 500;
  letter-spacing: 1px;
  text-align: center;
  text-decoration: none;
  background: -webkit-gradient(
    linear,
    left top,
    left bottom,
    from(#cba4c9),
    to(#e3b6e1)
  );
  border-radius: 5px;
  border: 1px solid #737b8d;

  :hover {
    background: -webkit-gradient(
      linear,
      left top,
      left bottom,
      from(#e3b6e1),
      to(#cba4c9)
    );
  }
`;

export default PinkButton;
