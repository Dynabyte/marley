import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import ErrorMessage from './components/ErrorMessage';
import Motion from './components/Motion';
import CaptureFrames from './components/register/CaptureFrames';
import Positioning from './components/register/PositionInformation';
import RegisterForm from './components/register/RegistrationForm';
import Home from './Home';
import GlobalStyle from './styling/GlobalStyle';

const App = () => (
  <>
    <GlobalStyle />
    <Router>
      <Switch>
        <Route path='/' exact>
          <Home />
        </Route>
        <Route path='/registration' exact>
          <RegisterForm />
        </Route>
        <Route path='/positioning' exact>
          <Positioning />
        </Route>
        <Route path='/capture-frames' exact>
          <CaptureFrames />
        </Route>
        <Route path='/motion' exact>
          <Motion />
        </Route>
        <Route path='/error' exact>
          <ErrorMessage />
        </Route>
      </Switch>
    </Router>
  </>
);

export default App;
