import {createContext, useEffect, useState} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil.js";

export const BackupContext = createContext({});

export const BackupProvider = (props) => {
    const [backups, setBackups] = useState([]);

    const updateBackups = () => jsonRequest("backups/").then((r) => setBackups(r.reverse()));

    useEffect(() => {
        updateBackups();
        const interval = setInterval(() => updateBackups(), 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <BackupContext.Provider value={{backups, updateBackups}}>
            {props.children}
        </BackupContext.Provider>
    )
}