import {
    Divider, Drawer,
    List,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Stack,
    Toolbar,
    Typography
} from "@mui/material";
import {sidebar} from "@/common/routes/server.jsx";
import {useLocation, useNavigate} from "react-router-dom";
import {Favorite} from "@mui/icons-material";

const drawerWidth = 240;
const DONATION_URL = "https://ko-fi.com/gnmyt";

export const Sidebar = ({mobileOpen, toggleOpen, window: containerWindow}) => {
    const location = useLocation();
    const navigate = useNavigate();

    const container = containerWindow !== undefined ? () => containerWindow().document.body : undefined;

    const isSelected = (path) => {
        if (path === "/") return location.pathname === "/";

        return location.pathname.startsWith(path);
    }

    const drawer = (
        <>
            <Toolbar>
                <Stack direction="row" alignItems="center" gap={1}>
                    <img src="/assets/img/favicon.png" alt="MCDash" width="40px" height="40px" />
                    <Typography variant="h5" noWrap fontWeight={700}>MCDash</Typography>
                </Stack>
            </Toolbar>

            <Divider />

            <List>
                {sidebar.map((route) => (
                    <ListItem key={route.path} disablePadding>
                        <ListItemButton selected={isSelected(route.path)} onClick={() => navigate(route.path.replace("/*", ""))}>
                            <ListItemIcon>{route.icon}</ListItemIcon>
                            <ListItemText primary={route.name} />
                        </ListItemButton>
                    </ListItem>
                ))}

                <Divider />

                <ListItem disablePadding>
                    <ListItemButton onClick={() => window.open(DONATION_URL, "_blank")}>
                        <ListItemIcon><Favorite color="error" /></ListItemIcon>
                        <ListItemText primary="Support the project" />
                    </ListItemButton>
                </ListItem>

            </List>
        </>
    );

    return (
        <>
            <Drawer container={container} variant="temporary" open={mobileOpen} onClose={toggleOpen}
                ModalProps={{keepMounted: true}} sx={{display: { xs: 'block', sm: 'none' },
                    '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth }}}>
                {drawer}
            </Drawer>
            <Drawer variant="permanent" sx={{display: { xs: 'none', sm: 'block' },
                    '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth }}} open>
                {drawer}
            </Drawer>
        </>
    )
}