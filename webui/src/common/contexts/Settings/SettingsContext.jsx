import {createContext, useState} from "react";
import i18n from "i18next";

export const SettingsContext = createContext({});

export const SettingsProvider = (props) => {

    const [theme, setTheme] = useState(localStorage.getItem("theme") || "dark");
    const [language, setLanguage] = useState(localStorage.getItem("language") || "en");

    const updateTheme = (theme) => {
        localStorage.setItem("theme", theme);
        setTheme(theme);
    }

    const updateLanguage = (language) => {
        localStorage.setItem("language", language);
        i18n.changeLanguage(language);
        setLanguage(language);
    }

    return (
        <SettingsContext.Provider value={{theme, updateTheme, language, updateLanguage}}>
            {props.children}
        </SettingsContext.Provider>
    )
}