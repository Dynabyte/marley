import React, { useState } from 'react';
import { IRegistrationData } from '../models/models';

type RegistrationDataType = {
  registrationData: IRegistrationData;
  setRegistrationData: React.Dispatch<React.SetStateAction<IRegistrationData>>;
};

export const RegistrationDataContext = React.createContext<
  RegistrationDataType
>(undefined);

export const RegistrationDataProvider = ({ children }) => {
  const [registrationData, setRegistrationData] = useState<IRegistrationData>({
    name: null,
    authCode: null,
  });

  return (
    <RegistrationDataContext.Provider
      value={{ registrationData, setRegistrationData }}
    >
      {children}
    </RegistrationDataContext.Provider>
  );
};
