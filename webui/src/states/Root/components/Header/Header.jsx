import {AppBar, IconButton, Stack, Toolbar, Tooltip, Typography} from "@mui/material";
import {ExitToApp} from "@mui/icons-material";
import {useContext, useEffect} from "react";
import {TokenContext} from "@contexts/Token/index.js";
import routes from "@/common/routes/server.jsx";
import {useLocation} from "react-router-dom";

const drawerWidth = 240;

export const Header = () => {
    const {checkToken} = useContext(TokenContext);
    const location = useLocation();

    useEffect(() => {
        document.title = "MCDash - " + getTitleByPath();
    }, [location]);

    const getTitleByPath = () => {
        const route = routes.find((route) => route.path === location.pathname);
        if (route) return route.name;
        return "Home";
    }

    const logout = () => {
        localStorage.removeItem("token");
        checkToken();
    }

    return (
        <AppBar position="fixed" sx={{ width: `calc(100% - ${drawerWidth}px)`, ml: `${drawerWidth}px` }}>
            <Toolbar>
                <Typography variant="h6" noWrap>{getTitleByPath()}</Typography>

                <Stack sx={{ml: "auto"}}>
                    <Tooltip title="Log out">
                        <IconButton onClick={logout}>
                            <ExitToApp />
                        </IconButton>
                    </Tooltip>
                </Stack>
            </Toolbar>
        </AppBar>
    )
}