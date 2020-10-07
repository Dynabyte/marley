import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Home from './Home';
import Analyze from './register/Analyze';
import Positioning from './register/PositionInformation';
import RegisterForm from './register/RegisterForm';

const App = () => (
  <Router>
    <Switch>
      <Route path='/' exact>
        <Home />
      </Route>
      <Route path='/register' exact>
        <RegisterForm />
      </Route>
      <Route path='/positioning' exact>
        <Positioning />
      </Route>
      <Route path='/analys' exact>
        <Analyze />
      </Route>
    </Switch>
  </Router>
);

export default App;
