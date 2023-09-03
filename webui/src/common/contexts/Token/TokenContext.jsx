import {useState, createContext, useEffect} from "react";
import {request} from "@/common/utils/RequestUtil.js";
import {Alert, Snackbar} from "@mui/material";
import {t} from "i18next";

export const TokenContext = createContext({});

export const TokenProvider = (props) => {
    const [tokenValid, setTokenValid] = useState(null);
    const [serverOnline, setServerOnline] = useState(null);

    const checkToken = () => request("ping").then((r) => {
        if (!r.ok && !(r.status === 400 || r.status === 401)) throw new Error("Server unavailable");
        setTokenValid(r.status === 200);
        setServerOnline(true);
        return r.status === 200;
    }).catch(() => {
        setServerOnline(false);
    });

    useEffect(() => {
        checkToken();
        const interval = setInterval(() => checkToken(), 10000);
        return () => clearInterval(interval);
    }, []);

    return (
        <TokenContext.Provider value={{tokenValid, checkToken, serverOnline}}>
            <Snackbar open={serverOnline === false && tokenValid !== null}>
                <Alert severity="error" sx={{width: "100%"}}>
                    {t("info.server_unavailable")}
                </Alert>
            </Snackbar>
            {props.children}
        </TokenContext.Provider>
    )
}