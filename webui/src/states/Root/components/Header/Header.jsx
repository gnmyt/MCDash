import {AppBar, IconButton, Stack, Toolbar, Typography} from "@mui/material";
import {AccountCircle, Menu as MenuIcon,} from "@mui/icons-material";
import {useEffect, useState} from "react";
import {sidebar} from "@/common/routes/server.jsx";
import {useLocation} from "react-router-dom";
import AccountMenu from "@/states/Root/components/Header/components/AccountMenu";

const drawerWidth = 240;

export const Header = ({toggleOpen}) => {
    const location = useLocation();

    const [menuOpen, setMenuOpen] = useState(false);

    useEffect(() => {
        document.title = "MCDash - " + getTitleByPath();
    }, [location]);

    const getTitleByPath = () => {
        const route = sidebar.find((route) => location.pathname.startsWith(route.path) && route.path !== "/");
        if (route) return route.name;
        return "Overview";
    }


    return (
        <AppBar position="fixed" sx={{width: {sm: `calc(100% - ${drawerWidth}px)`}, ml: {sm: `${drawerWidth}px`}}}>
            <AccountMenu menuOpen={menuOpen} setMenuOpen={setMenuOpen}/>

            <Toolbar>
                <IconButton aria-label="open drawer" edge="start" onClick={toggleOpen}
                            sx={{mr: 2, display: {sm: 'none'}}}>
                    <MenuIcon/>
                </IconButton>
                <Typography variant="h6" noWrap>{getTitleByPath()}</Typography>

                <Stack sx={{ml: "auto"}}>
                    <IconButton id="menu" onClick={() => setMenuOpen(true)}>
                        <AccountCircle/>
                    </IconButton>
                </Stack>
            </Toolbar>
        </AppBar>
    )
}