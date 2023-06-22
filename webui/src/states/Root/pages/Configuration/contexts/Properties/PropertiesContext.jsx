import {useState, useEffect, createContext} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil";

export const PropertiesContext = createContext({});

export const PropertiesProvider = (props) => {

    const [properties, setProperties] = useState([]);

    const updateProperties = () => {
        jsonRequest("manage/properties").then((r) => setProperties(r.sort((a, b) => a.name.localeCompare(b.name))));
    }

    useEffect(() => {
        updateProperties();
        const interval = setInterval(() => updateProperties(), 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <PropertiesContext.Provider value={{properties, updateProperties}}>
            {props.children}
        </PropertiesContext.Provider>
    );
}