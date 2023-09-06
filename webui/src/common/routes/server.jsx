import {Dashboard, Extension, Folder, Group, Public, Save, Settings, Terminal, Event} from "@mui/icons-material";
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
import {SSHStatusProvider} from "@/states/Root/pages/Configuration/contexts/SSHStatus";
import Worlds from "@/states/Root/pages/Worlds";
import {WorldsProvider} from "@/states/Root/pages/Worlds/contexts/Worlds";
import {t} from "i18next";
import Scheduler from "@/states/Root/pages/Scheduler";
import {ScheduleProvider} from "@/states/Root/pages/Scheduler/contexts/Schedules/SchedulesContext.jsx";

export const routes = [
    {path: "/", element: <StatsProvider><Overview /></StatsProvider>},
    {path: "/players", element: <BanListProvider><WhiteListProvider><Players /></WhiteListProvider></BanListProvider>},
    {path: "/files/*", element: <Files />},
    {path: "/console", element: <Console />},
    {path: "/worlds", element: <WorldsProvider><Worlds /></WorldsProvider>},
    {path: "/plugins", element: <PluginsProvider><Plugins /></PluginsProvider>},
    {path: "/backups", element: <BackupProvider><Backups/></BackupProvider>},
    {path: "/schedules", element: <ScheduleProvider><Scheduler /></ScheduleProvider>},
    {path: "/configuration", element: <PropertiesProvider><SSHStatusProvider><Configuration /></SSHStatusProvider></PropertiesProvider>}
]

export const sidebar = [
    {
        path: "/",
        icon: <Dashboard />,
        name: () => t("nav.overview")
    },
    {
        path: "/players",
        icon: <Group />,
        name: () => t("nav.players")
    },
    {
        path: "/files",
        icon: <Folder />,
        name: () => t("nav.files")
    },
    {
        path: "/console",
        icon: <Terminal />,
        name: () => t("nav.console")
    },
    {
        path: "/worlds",
        icon: <Public />,
        name: () => t("nav.worlds")
    },
    {
        path: "/plugins",
        icon: <Extension />,
        name: () => t("nav.plugins")
    },
    {
        path: "/backups",
        icon: <Save />,
        name: () => t("nav.backups")
    },
    {
        path: "/schedules",
        icon: <Event />,
        name: () => t("nav.schedules")
    },
    {
        path: "/configuration",
        icon: <Settings />,
        name: () => t("nav.configuration")
    }
]