import {useState, createContext, useEffect} from "react";
import {request} from "@/common/utils/RequestUtil.js";

export const TokenContext = createContext({});

export const TokenProvider = (props) => {

    const [tokenValid, setTokenValid] = useState(null);

    const checkToken = () => request("/speedtests/status").then((r) => {
        setTokenValid(r.status === 200);
        return r.status === 200;
    });

    useEffect(() => {
        checkToken();
    }, []);

    return (
        <TokenContext.Provider value={{tokenValid, checkToken}}>
            {props.children}
        </TokenContext.Provider>
    )
}