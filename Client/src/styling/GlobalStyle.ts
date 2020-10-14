import { createGlobalStyle } from 'styled-components';
import stockholm from '../static/images/stockholm.jpg';

const GlobalStyle = createGlobalStyle`
:root {
  --image-url: url(${stockholm});
}

* {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }


html,
body {
  height: 100vh;
}

body {
  font-family: zillaslab,Palatino,"Palatino Linotype",x-locale-heading-secondary,serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  font-size: 16px;
  color: white;
  margin: 0;
  padding: 0;
  width: 100vw;
  background: linear-gradient(rgb(239, 87, 227, 0.9), rgba(239, 87, 227, 0.1)),
    var(--image-url) center center;
}
`;

export default GlobalStyle;
