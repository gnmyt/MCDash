import {useState, useEffect, createContext} from "react";
import {jsonRequest, patchRequest} from "@/common/utils/RequestUtil";

export const WhiteListContext = createContext({});

export const WhiteListProvider = (props) => {

    const [whitelistedPlayers, setWhitelistedPlayers] = useState([]);
    const [whitelistActive, setWhitelistActive] = useState(false);

    const updatePlayers = () => {
        jsonRequest("players/whitelist").then(r => {
            if (!Array.isArray(r)) return;
            setWhitelistedPlayers(r)
        });

        jsonRequest("action/whitelist").then(r => setWhitelistActive(r.status));
    }

    useEffect(() => {
        updatePlayers();
        const interval = setInterval(() => updatePlayers(), 5000);
        return () => clearInterval(interval);
    }, []);

    const switchWhitelist = () => {
        patchRequest("action/whitelist", {status: !whitelistActive})
            .then(() => updatePlayers());
    }

    return (
        <WhiteListContext.Provider value={{whitelistedPlayers, whitelistActive, updatePlayers, switchWhitelist}}>
            {props.children}
        </WhiteListContext.Provider>
    );
}