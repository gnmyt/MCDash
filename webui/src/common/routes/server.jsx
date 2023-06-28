import {Dashboard, Extension, Folder, Group, Save, Settings, Terminal} from "@mui/icons-material";
import Overview from "@/states/Root/pages/Overview";
import {BanListProvider} from "@/states/Root/pages/Players/contexts/BanList";
import Players from "@/states/Root/pages/Players";
import Files from "@/states/Root/pages/Files";
import { WhiteListProvider } from "@/states/Root/pages/Players/contexts/WhiteList";
import Console from "@/states/Root/pages/Console";
import Plugins from "@/states/Root/pages/Plugins";
import {StatsProvider} from "@/states/Root/pages/Overview/contexts/StatsContext";
import {PluginsProvider} from "@/states/Root/pages/Plugins/contexts/Plugins";
import Configuration from "@/states/Root/pages/Configuration";
import {PropertiesProvider} from "@/states/Root/pages/Configuration/contexts/Properties";
import Backups from "@/states/Root/pages/Backups";
import {BackupProvider} from "@/states/Root/pages/Backups/contexts/Backups";

export const routes = [
    {path: "/", element: <StatsProvider><Overview /></StatsProvider>},
    {path: "/players", element: <BanListProvider><WhiteListProvider><Players /></WhiteListProvider></BanListProvider>},
    {path: "/files/*", element: <Files />},
    {path: "/console", element: <Console />},
    {path: "/plugins", element: <PluginsProvider><Plugins /></PluginsProvider>},
    {path: "/backups", element: <BackupProvider><Backups/></BackupProvider>},
    {path: "/configuration", element: <PropertiesProvider><Configuration /></PropertiesProvider>}
]

export const sidebar = [
    {
        path: "/",
        icon: <Dashboard />,
        name: "Overview"
    },
    {
        path: "/players",
        icon: <Group />,
        name: "Players"
    },
    {
        path: "/files",
        icon: <Folder />,
        name: "File Manager"
    },
    {
        path: "/console",
        icon: <Terminal />,
        name: "Console"
    },
    {
        path: "/plugins",
        icon: <Extension />,
        name: "Plugins"
    },
    {
        path: "/backups",
        icon: <Save />,
        name: "Backups"
    },
    {
        path: "/configuration",
        icon: <Settings />,
        name: "Configuration"
    }
]