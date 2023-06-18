import {Dashboard, Extension, Folder, Group, Public, Settings, Terminal} from "@mui/icons-material";
import Overview from "@/states/Root/pages/Overview";

export default [
    {
        path: "/",
        element: <Overview />,
        icon: <Dashboard />,
        name: "Overview"
    },
    {
        path: "/players",
        element: <h2>Players</h2>,
        icon: <Group />,
        name: "Players"
    },
    {
        path: "/files",
        element: <h2>Files</h2>,
        icon: <Folder />,
        name: "File Manager"
    },
    {
        path: "/console",
        element: <h2>Console</h2>,
        icon: <Terminal />,
        name: "Console"
    },
    {
        path: "/plugins",
        element: <h2>Plugins</h2>,
        icon: <Extension />,
        name: "Plugins"
    },
    {
        path: "/worlds",
        element: <h2>Worlds</h2>,
        icon: <Public />,
        name: "Worlds"
    },
    {
        path: "/configuration",
        element: <h2>Configuration</h2>,
        icon: <Settings />,
        name: "Configuration"
    }
]