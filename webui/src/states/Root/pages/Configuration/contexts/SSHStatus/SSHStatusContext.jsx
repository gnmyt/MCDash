import {createContext, useEffect, useState} from "react";
import {jsonRequest, patchRequest} from "@/common/utils/RequestUtil.js";
import {Alert, Snackbar} from "@mui/material";
import {t} from "i18next";

export const SSHStatusContext = createContext({});

export const SSHStatusProvider = (props) => {

    const [sshPort, setSshPort] = useState(5174);
    const [sshStatus, setSshStatus] = useState(false);

    const [changesSaved, setChangesSaved] = useState(false);
    const [bindError, setBindError] = useState(false);

    const updateStatus = () => {
        return jsonRequest("services/ssh").then((response) => {
            setSshStatus(response.enabled);
            setSshPort(response.port);

            return response;
        });
    }

    const updateSshStatus = () => {
        setSshStatus(!sshStatus);
        patchRequest("services/ssh", {enabled: !sshStatus}).then(() => {
            updateStatus().then((r) => {
                if (r.enabled === sshStatus) return setBindError(true);
                setChangesSaved(true);
            });
        });
    }

    const updateSshPort = (event) => {
        if (parseInt(event.target.value) === sshPort) return;
        setSshPort(event.target.value);
        patchRequest("services/ssh", {port: event.target.value}).then(() => {
            updateStatus().then((r) => {
                if (r.port === sshPort) return setBindError(true);
                setChangesSaved(true);
            });
        });
    }

    useEffect(() => {
        updateStatus();
        const interval = setInterval(() => updateStatus(), 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <SSHStatusContext.Provider value={{sshPort, sshStatus, updateStatus, updateSshStatus, updateSshPort}}>
            <Snackbar open={bindError} autoHideDuration={3000} onClose={() => setBindError(false)}
                        anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setBindError(false)} severity="error" sx={{width: '100%'}}>
                    {t("configuration.ssh.bind_error")}
                </Alert>
            </Snackbar>

            <Snackbar open={changesSaved} autoHideDuration={3000} onClose={() => setChangesSaved(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setChangesSaved(false)} severity="success" sx={{width: '100%'}}>
                    {t("action.changes_saved")}
                </Alert>
            </Snackbar>

            {props.children}
        </SSHStatusContext.Provider>
    );
}