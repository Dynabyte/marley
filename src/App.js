import {
  detectSingleFace,
  loadSsdMobilenetv1Model,
  SsdMobilenetv1Options,
} from "face-api.js";
import React from "react";



function App() {

  var list = [];
  for (var i = 145; i <= 375; i++) {
    list.push(i);
  }


  return (
    <div>
      {list.map((_,index) => 
        <img src={"images/daniel-vardagsrum1/0" + _ + ".jpg"}/>
  
    )};
    </div>
   
    
  );
  }



export default App;
