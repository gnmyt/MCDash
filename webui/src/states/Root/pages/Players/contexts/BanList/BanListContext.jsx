import {useState, useEffect, createContext} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil";

export const BanListContext = createContext({});

export const BanListProvider = (props) => {

    const [bannedPlayers, setBannedPlayers] = useState([]);

    const updatePlayers = () => {
        jsonRequest("players/banlist").then(r => {
            if (!Array.isArray(r)) return;
            setBannedPlayers(r)
        });
    }

    useEffect(() => {
        updatePlayers();
        const interval = setInterval(() => updatePlayers(), 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <BanListContext.Provider value={{bannedPlayers, updatePlayers}}>
            {props.children}
        </BanListContext.Provider>
    );
}