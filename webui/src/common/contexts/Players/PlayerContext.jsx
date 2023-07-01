import {useState, useEffect, createContext} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil";

export const PlayerContext = createContext({});

export const PlayerProvider = (props) => {

    const [players, setPlayers] = useState([]);

    const updatePlayers = () => {
        jsonRequest("players/online").then(r => {
            if (!Array.isArray(r)) return;
            setPlayers(r)
        });
    }

    useEffect(() => {
        updatePlayers();
        const interval = setInterval(() => updatePlayers(), 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <PlayerContext.Provider value={{players, updatePlayers}}>
            {props.children}
        </PlayerContext.Provider>
    );
}