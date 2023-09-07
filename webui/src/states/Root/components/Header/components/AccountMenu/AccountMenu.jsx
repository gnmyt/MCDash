import {Avatar, Divider, ListItemIcon, Menu, MenuItem, Typography} from "@mui/material";
import {Brightness4, Brightness7, Favorite, Logout, Translate} from "@mui/icons-material";
import {useContext, useState} from "react";
import {SettingsContext} from "@contexts/Settings";
import {TokenContext} from "@contexts/Token";
import {t} from "i18next";
import ChangeLanguageDialog from "./components/ChangeLanguageDialog";

const DONATION_URL = "https://ko-fi.com/gnmyt";

export const AccountMenu = ({menuOpen, setMenuOpen}) => {
    const {checkToken} = useContext(TokenContext);
    const {theme, updateTheme} = useContext(SettingsContext);

    const [languageOpen, setLanguageOpen] = useState(false);

    const retrieveUsername = () => atob(localStorage.getItem("token")).split(":")[0];

    const switchTheme = () => {
        setMenuOpen(false);
        updateTheme(theme === 'dark' ? 'light' : 'dark');
    }

    const logout = () => {
        setMenuOpen(false);
        localStorage.removeItem("token");
        checkToken();
    }

    const openDonation = () => {
        setMenuOpen(false);
        window.open(DONATION_URL, "_blank");
    }

    const openLanguage = () => {
        setMenuOpen(false);
        setLanguageOpen(true);
    }

    return (
        <>
            <ChangeLanguageDialog open={languageOpen} setOpen={setLanguageOpen} setMenuOpen={setMenuOpen}/>
            <Menu anchorEl={document.getElementById("menu")} open={menuOpen} onClose={() => setMenuOpen(false)}>
                <MenuItem>
                    <Avatar sx={{width: 24, height: 24, mr: 1}} src={"https://mc-heads.net/avatar/" + retrieveUsername()}
                            alt={retrieveUsername()}>
                    </Avatar>
                    <Typography variant="inherit">{retrieveUsername()}</Typography>
                </MenuItem>
                <Divider/>
                <MenuItem onClick={openDonation}>
                    <ListItemIcon>
                        <Favorite color="error" />
                    </ListItemIcon>
                    {t("header.support_me")}
                </MenuItem>
                <MenuItem onClick={openLanguage}>
                    <ListItemIcon>
                        <Translate fontSize="small"/>
                    </ListItemIcon>
                    {t("header.language")}
                </MenuItem>
                <MenuItem onClick={switchTheme}>
                    <ListItemIcon>
                        {theme === 'dark' ? <Brightness7 size="small" /> : <Brightness4 size="small" />}
                    </ListItemIcon>
                    {t(`header.${theme === 'dark' ? 'light' : 'dark'}_theme`)}
                </MenuItem>
                <MenuItem onClick={logout}>
                    <ListItemIcon>
                        <Logout fontSize="small"/>
                    </ListItemIcon>
                    {t("header.logout")}
                </MenuItem>
            </Menu>
        </>
    )
}