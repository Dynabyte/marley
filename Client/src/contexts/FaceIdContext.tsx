import React, {useState} from "react";



export const FaceIdContext = React.createContext(null);

export const FaceIdProvider = (props) => {
    const [faceId, setFaceId] = useState(null);

    //Renders all the child components
    return(
        <FaceIdContext.Provider value={[faceId, setFaceId]}>
            {props.children}
        </FaceIdContext.Provider>
    );
}