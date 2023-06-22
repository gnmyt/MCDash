import {useState, useEffect, createContext} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil";

export const PluginsContext = createContext({});

export const PluginsProvider = (props) => {

    const [plugins, setPlugins] = useState([]);

    const updatePlugins = () => {
        jsonRequest("plugin/list").then((r) => setPlugins(r));
    }

    useEffect(() => {
        updatePlugins();
        const interval = setInterval(() => updatePlugins(), 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <PluginsContext.Provider value={{plugins, updatePlugins}}>
            {props.children}
        </PluginsContext.Provider>
    );
}