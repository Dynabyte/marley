import React from 'react';
import styled from 'styled-components';
import { ICheckboxProps } from '../models/models';

const CheckboxContainer = styled.div`
  position: relative;
  display: inline-block;
  vertical-align: middle;
`;

const Icon = styled.svg`
  fill: none;
  stroke: white;
  stroke-width: 2px;
`;

const HiddenCheckbox = styled.input.attrs({ type: 'checkbox' })`
  opacity: 0;
`;

const StyledCheckbox = styled.div<ICheckboxProps>`
  position: absolute;
  left: 0;
  top: -5px;
  display: inline-block;
  width: 1.5rem;
  height: 1.5rem;
  background: ${({ checked }) => (checked ? '#777' : '#fbfbfb')};
  margin: 0 0.5rem;
  border-radius: 3px;
  transition: all 150ms;
  border: 1px solid black;

  ${Icon} {
    visibility: ${({ checked }) => (checked ? 'visible' : 'hidden')};
  }
`;

const Checkbox = ({ checked, ...props }) => (
  <CheckboxContainer>
    <HiddenCheckbox {...props} />
    <StyledCheckbox checked={checked}>
      <Icon viewBox='0 0 24 24'>
        <polyline points='20 6 9 17 4 12' />
      </Icon>
    </StyledCheckbox>
  </CheckboxContainer>
);

export default Checkbox;
