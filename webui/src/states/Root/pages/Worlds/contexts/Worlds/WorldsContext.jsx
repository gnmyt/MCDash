import {createContext, useEffect, useState} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil.js";

export const WorldsContext = createContext({});

export const WorldsProvider = (props) => {

    const [worlds, setWorlds] = useState([]);

    const updateWorlds = () => {
        jsonRequest("worlds/")
            .then((data) => setWorlds(data));
    }

    useEffect(() => {
        updateWorlds();
        const interval = setInterval(updateWorlds, 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <WorldsContext.Provider value={{worlds, updateWorlds}}>
            {props.children}
        </WorldsContext.Provider>
    );
}