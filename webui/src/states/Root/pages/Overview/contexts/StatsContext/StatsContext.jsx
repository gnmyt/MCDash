import {useState, useEffect, createContext} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil";

export const StatsContext = createContext({});

export const StatsProvider = (props) => {

    const [stats, setStats] = useState({});

    const updateStats = () => {
        jsonRequest("stats/").then(r => setStats(r));
    }

    useEffect(() => {
        updateStats();
        const interval = setInterval(() => updateStats(), 1000);
        return () => clearInterval(interval);
    }, []);

    return (
        <StatsContext.Provider value={{stats, updateStats}}>
            {props.children}
        </StatsContext.Provider>
    );
}