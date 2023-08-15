import {createContext, useState} from "react";

export const SettingsContext = createContext({});

export const SettingsProvider = (props) => {

    const [theme, setTheme] = useState(localStorage.getItem("theme") || "dark");

    const updateTheme = (theme) => {
        localStorage.setItem("theme", theme);
        setTheme(theme);
    }

    return (
        <SettingsContext.Provider value={{theme, updateTheme}}>
            {props.children}
        </SettingsContext.Provider>
    )
}