import {AppBar, Avatar, IconButton, Stack, Toolbar, Tooltip, Typography} from "@mui/material";
import {BrowserUpdated, Menu as MenuIcon} from "@mui/icons-material";
import {useEffect, useState} from "react";
import {sidebar} from "@/common/routes/server.jsx";
import {useLocation} from "react-router-dom";
import AccountMenu from "@/states/Root/components/Header/components/AccountMenu";
import {t} from "i18next";
import UpdateDialog from "@/states/Root/components/Header/components/UpdateDialog";
import {jsonRequest} from "@/common/utils/RequestUtil.js";

const drawerWidth = 240;

export const Header = ({toggleOpen}) => {
    const location = useLocation();

    const retrieveUsername = () => atob(localStorage.getItem("token")).split(":")[0];

    const [versionInfo, setVersionInfo] = useState({available: false});
    const [updateDialogOpen, setUpdateDialogOpen] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);

    useEffect(() => {
        document.title = "MCDash - " + getTitleByPath();
    }, [location]);

    useEffect(() => {
        jsonRequest("update").then((data) => setVersionInfo(data));
    }, []);

    const getTitleByPath = () => {
        const route = sidebar.find((route) => location.pathname.startsWith(route.path) && route.path !== "/");
        if (route) return route.name();
        return t("nav.overview");
    }


    return (
        <AppBar position="fixed" sx={{width: {sm: `calc(100% - ${drawerWidth}px)`}, ml: {sm: `${drawerWidth}px`}}}>
            <AccountMenu menuOpen={menuOpen} setMenuOpen={setMenuOpen}/>
            <UpdateDialog open={updateDialogOpen} setOpen={setUpdateDialogOpen} setVersionInfo={setVersionInfo}
                            latest={versionInfo.latest} current={versionInfo.current}/>

            <Toolbar>
                <IconButton aria-label="open drawer" edge="start" onClick={toggleOpen}
                            sx={{mr: 2, display: {sm: 'none'}}}>
                    <MenuIcon/>
                </IconButton>
                <Typography variant="h6" noWrap>{getTitleByPath()}</Typography>

                <Stack sx={{ml: "auto"}} direction="row">
                    {versionInfo.available && <Tooltip title={t("update.available")}>
                        <IconButton color="warning" onClick={() => setUpdateDialogOpen(true)}>
                            <BrowserUpdated/>
                        </IconButton>
                    </Tooltip>}
                    <IconButton id="menu" onClick={() => setMenuOpen(true)}>
                        <Avatar src={"https://mc-heads.net/avatar/" + retrieveUsername()} alt={retrieveUsername()}
                                sx={{width: 24, height: 24}}/>
                    </IconButton>
                </Stack>
            </Toolbar>
        </AppBar>
    )
}