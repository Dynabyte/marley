import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Home from './Home';
import Analyze from './components/register/CaptureFrames';
import Positioning from './components/register/PositionInformation';
import RegisterForm from './components/register/RegistrationForm';

const App = () => (
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
        <Analyze />
      </Route>
    </Switch>
  </Router>
);

export default App;
