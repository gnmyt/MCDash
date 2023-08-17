import { useState, useEffect, createContext } from "react";
import { jsonRequest } from "@/common/utils/RequestUtil";

export const StatsContext = createContext({});

export const StatsProvider = (props) => {
    const [stats, setStats] = useState([]);

    const updateStats = () => {
        jsonRequest("stats/").then((r) => {
            setStats((prevStats) => {
                const newStats = [...prevStats, {date: new Date(), ...r}];
                if (newStats.length > 20)
                    newStats.shift();
                return newStats;
            });
        });
    };

    useEffect(() => {
        updateStats();
        const interval = setInterval(() => updateStats(), 1000);
        return () => clearInterval(interval);
    }, []);

    return (
        <StatsContext.Provider value={{ stats, updateStats }}>
            {props.children}
        </StatsContext.Provider>
    );
};