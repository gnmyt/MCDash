import {ArchiveIcon, FolderOpenIcon, SquaresFourIcon, WifiHighIcon, HardDrivesIcon, GearSixIcon, TerminalIcon} from "@phosphor-icons/react";
import {t} from "i18next";
import FileManager from "@/states/Root/pages/FileManager/FileManager.tsx";
import Overview from "@/states/Root/pages/Overview/Overview.tsx";
import ServerSettings from "@/states/Root/pages/ServerSettings/ServerSettings.tsx";
import SSHSettings from "@/states/Root/pages/SSHSettings/SSHSettings.tsx";
import Backups from "@/states/Root/pages/Backups/Backups.tsx";
import Console from "@/states/Root/pages/Console/Console.tsx";

export const sidebar = [
    {
        path: "/",
        icon: SquaresFourIcon,
        element: <Overview />,
        name: () => t("nav.overview")
    },
    {
        routerPath: "/files/*",
        path: "/files/",
        icon: FolderOpenIcon,
        requiredFeatures: ["FileManager"],
        element: <FileManager/>,
        name: () => t("nav.files")
    },
    {
        path: "/console",
        icon: TerminalIcon,
        requiredFeatures: ["Console"],
        element: <Console />,
        name: () => t("nav.console")
    },
    {
        path: "/backups",
        name: () => t("nav.backups"),
        icon: ArchiveIcon,
        requiredFeatures: ["Backups"],
        element: <Backups />
    },
    {
        path: "/settings",
        name: () => t("nav.settings.base"),
        icon: GearSixIcon,
        items: [
            {
                path: "/settings/server",
                name: () => t("nav.settings.server"),
                icon: HardDrivesIcon,
                requiredFeatures: ["Properties"],
                element: <ServerSettings />
            },
            {
                path: "/settings/ssh",
                name: () => t("nav.settings.ssh"),
                icon: WifiHighIcon,
                requiredFeatures: ["SSH"],
                element: <SSHSettings />
            }
        ]
    }
];

export const getLocationByPath = (path: string) => {
    for (const item of sidebar) {
        if (item.path !== "/" && path.startsWith(item.path)) return item;
        if (item.path === path) return item;
        if (item.items) {
            for (const subitem of item.items) {
                if (subitem.path === path) return subitem;
            }
        }
    }
    return null;
}

export const getRoutes = () => {
    const routes = [];
    for (const item of sidebar) {
        if (item.items) {
            for (const subitem of item.items) {
                routes.push(subitem);
            }
        } else {
            const newItem = {...item};
            newItem.path = item.routerPath || item.path;
            routes.push(newItem);
        }
    }
    return routes;
}