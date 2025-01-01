import {LayoutDashboardIcon} from "lucide-react";
import {t} from "i18next";

export const routes = [
    {path: "/", element: <div>Home</div>},
]

export const sidebar = [
    {
        path: "/",
        icon: <LayoutDashboardIcon />,
        name: () => t("nav.overview")
    }
];