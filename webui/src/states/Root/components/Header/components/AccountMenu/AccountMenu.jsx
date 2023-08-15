import {Avatar, Divider, ListItemIcon, Menu, MenuItem, Typography} from "@mui/material";
import {Brightness4, Brightness7, Favorite, Logout} from "@mui/icons-material";
import {useContext} from "react";
import {SettingsContext} from "@contexts/Settings";
import {TokenContext} from "@contexts/Token";

const DONATION_URL = "https://ko-fi.com/gnmyt";

export const AccountMenu = ({menuOpen, setMenuOpen}) => {
    const {checkToken} = useContext(TokenContext);
    const {theme, updateTheme} = useContext(SettingsContext);

    const retrieveUsername = () => atob(localStorage.getItem("token")).split(":")[0];

    const switchTheme = () => {
        updateTheme(theme === 'dark' ? 'light' : 'dark');
        setMenuOpen(false);
    }

    const logout = () => {
        localStorage.removeItem("token");
        checkToken();
        setMenuOpen(false);
    }

    const openDonation = () => {
        window.open(DONATION_URL, "_blank");
        setMenuOpen(false);
    }

    return (
        <Menu anchorEl={document.getElementById("menu")} open={menuOpen}
              onClose={() => setMenuOpen(false)}>
            <MenuItem>
                <Avatar sx={{width: 24, height: 24, mr: 1}}>
                    {retrieveUsername().charAt(0)}
                </Avatar>
                <Typography variant="inherit">{retrieveUsername()}</Typography>
            </MenuItem>
            <Divider/>
            <MenuItem onClick={openDonation}>
                <ListItemIcon>
                    <Favorite color="error" />
                </ListItemIcon>
                Support me
            </MenuItem>
            <MenuItem onClick={switchTheme}>
                <ListItemIcon>
                    {theme === 'dark' ? <Brightness7 size="small" /> : <Brightness4 size="small" />}
                </ListItemIcon>
                {theme === 'dark' ? 'Light theme' : 'Dark theme'}
            </MenuItem>
            <MenuItem onClick={logout}>
                <ListItemIcon>
                    <Logout fontSize="small"/>
                </ListItemIcon>
                Logout
            </MenuItem>
        </Menu>
    )
}