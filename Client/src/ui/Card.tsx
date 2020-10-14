import styled from 'styled-components';

const Card = styled.div`
  padding: 0px 25px 25px;
  background: #fff;
  box-shadow: 0px 0px 0px 5px rgba(255, 255, 255, 0.4),
    0px 4px 20px rgba(0, 0, 0, 0.33);
  border-radius: 5px;
  width: 40rem;

  @media screen and (max-width: 575.98px) {
    width: 95%;
  }
`;

export default Card;
