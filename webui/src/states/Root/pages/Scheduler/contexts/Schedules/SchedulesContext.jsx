import {createContext, useEffect, useState} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil.js";

export const SchedulesContext = createContext({});

export const ScheduleProvider = ({children}) => {

    const [schedules, setSchedules] = useState([]);

    const updateSchedules = () => {
        jsonRequest("schedules/").then((res) => setSchedules(res));
    }

    useEffect(() => {
        updateSchedules();

        const interval = setInterval(() => updateSchedules(), 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <SchedulesContext.Provider value={{schedules, updateSchedules}}>
            {children}
        </SchedulesContext.Provider>
    )
}