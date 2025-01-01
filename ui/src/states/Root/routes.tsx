import {Folders, LayoutDashboardIcon, ServerIcon, Settings2} from "lucide-react";
import {t} from "i18next";

export const routes = [
    {
        path: "/", element:
            <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
                <div className="grid auto-rows-min gap-4 md:grid-cols-3">
                    <div className="aspect-video rounded-xl bg-muted/50"/>
                    <div className="aspect-video rounded-xl bg-muted/50"/>
                    <div className="aspect-video rounded-xl bg-muted/50"/>
                </div>
                <div className="min-h-[100vh] flex-1 rounded-xl bg-muted/50 md:min-h-min"/>
            </div>
    },
    {
        path: "/files", element: <h2>Files</h2>
    },
    {
        path: "/settings/server", element: <h2>Server</h2>
    },
];

export const sidebar = [
    {
        path: "/",
        icon: LayoutDashboardIcon,
        name: () => t("nav.overview")
    },
    {
        path: "/files",
        icon: Folders,
        requiredFeatures: ["FileManager"],
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
            }
        ]
    }
];

export const getLocationByPath = (path: string) => {
    for (const item of sidebar) {
        if (item.path === path) return item;
        if (item.items) {
            for (const subitem of item.items) {
                if (subitem.path === path) return subitem;
            }
        }
    }
    return null;
}