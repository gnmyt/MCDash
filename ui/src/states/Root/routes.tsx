import {Folders, LayoutDashboardIcon, ServerIcon, Settings2} from "lucide-react";
import {t} from "i18next";
import FileManager from "@/states/Root/pages/FileManager/FileManager.tsx";
import Overview from "@/states/Root/pages/Overview/Overview.tsx";

export const sidebar = [
    {
        path: "/",
        icon: LayoutDashboardIcon,
        element: <Overview />,
        name: () => t("nav.overview")
    },
    {
        routerPath: "/files/*",
        path: "/files/",
        icon: Folders,
        requiredFeatures: ["FileManager"],
        element: <FileManager/>,
        name: () => t("nav.files")
    },
    {
        path: "/settings",
        name: () => t("nav.settings.base"),
        icon: Settings2,
        items: [
            {
                path: "/settings/server",
                name: () => t("nav.settings.server"),
                icon: ServerIcon,
                element: <div>Server</div>
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