import React, { useState } from 'react';

type RegistrationDataType = {
  registrationData: RegistrationData;
  setRegistrationData: React.Dispatch<React.SetStateAction<RegistrationData>>;
};

interface RegistrationData {
  name: string;
  authCode: string;
}

export const RegistrationDataContext = React.createContext<
  RegistrationDataType
>(undefined);

export const RegistrationDataProvider = ({ children }) => {
  const [registrationData, setRegistrationData] = useState<RegistrationData>({
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
