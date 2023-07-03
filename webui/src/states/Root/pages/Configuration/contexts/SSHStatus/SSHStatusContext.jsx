import {createContext, useEffect, useState} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil.js";

export const SSHStatusContext = createContext({});

export const SSHStatusProvider = (props) => {

    const [sshPort, setSshPort] = useState(22);
    const [sshStatus, setSshStatus] = useState(false);

    const updateStatus = () => {
        jsonRequest("services/ssh").then((response) => {
            setSshStatus(response.enabled);
            setSshPort(response.port);
        });
    }

    useEffect(() => {
        updateStatus();
        const interval = setInterval(() => updateStatus(), 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <SSHStatusContext.Provider value={{sshPort, sshStatus, setSshPort, updateStatus}}>
            {props.children}
        </SSHStatusContext.Provider>
    );
}