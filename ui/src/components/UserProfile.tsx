"use client"
import {ChevronsUpDown, HeartIcon, LanguagesIcon, LogOut, Moon, Sun} from "lucide-react"

import {Avatar, AvatarFallback, AvatarImage,} from "@/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem, DropdownMenuPortal,
    DropdownMenuSeparator, DropdownMenuSub, DropdownMenuSubContent, DropdownMenuSubTrigger,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    useSidebar,
} from "@/components/ui/sidebar"
import {ServerInfoContext} from "@/contexts/ServerInfoContext.tsx";
import {useContext} from "react";
import {postRequest} from "@/lib/RequestUtil.ts";
import {SUPPORT_URL} from "@/App.tsx";
import { useTheme } from "@/components/theme-provider"
import {languages} from "@/i18n.ts";
import Flag from 'react-world-flags';
import i18n, {t} from "i18next";


export function UserProfile() {
    const {isMobile} = useSidebar();

    const {serverInfo, checkToken} = useContext(ServerInfoContext)!;

    const {setTheme, theme} = useTheme();

    const logout = () => {
        postRequest("session/destroy", {session: localStorage.getItem("sessionToken")}).then(async () => {
            localStorage.removeItem("sessionToken");

            await checkToken();
        });
    }

    const changeLanguage = (language: string) => {
        localStorage.setItem("language", language);
        i18n.changeLanguage(language);
    }

    if (!serverInfo.accountName) {
        return null;
    }

    return (
        <SidebarMenu>
            <SidebarMenuItem>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <SidebarMenuButton size="lg"
                            className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground">
                            <Avatar className="h-8 w-8 rounded-lg">
                                <AvatarImage src={"https://minotar.net/avatar/" + serverInfo.accountName + ".png"} alt={serverInfo.accountName}/>
                                <AvatarFallback className="rounded-lg">{serverInfo.accountName?.charAt(0).toUpperCase()}</AvatarFallback>
                            </Avatar>
                            <div className="grid flex-1 text-left text-sm leading-tight">
                                <span className="truncate font-semibold">{serverInfo.accountName}</span>
                                <span className="truncate text-xs">{t("header.account")}</span>
                            </div>
                            <ChevronsUpDown className="ml-auto size-4"/>
                        </SidebarMenuButton>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent className="w-[--radix-dropdown-menu-trigger-width] min-w-56 rounded-lg"
                        side={isMobile ? "bottom" : "right"} align="end" sideOffset={4}>
                        <DropdownMenuGroup>
                            <DropdownMenuItem className="text-red-600" onClick={() => window.open(SUPPORT_URL, "_blank")}>
                                <HeartIcon/>
                                {t("header.support_me")}
                            </DropdownMenuItem>
                        </DropdownMenuGroup>
                        <DropdownMenuSeparator/>
                        <DropdownMenuGroup>
                            <DropdownMenuItem onClick={() => setTheme(theme === "dark" ? "light" : "dark")}>
                                {theme === "dark" ? <Sun/> : <Moon/>}
                                {theme === "dark" ? t("header.light_theme") : t("header.dark_theme")}
                            </DropdownMenuItem>
                            <DropdownMenuSub>
                                <DropdownMenuSubTrigger>
                                    <LanguagesIcon/>
                                    {t("header.update_language")}
                                </DropdownMenuSubTrigger>
                                <DropdownMenuPortal>
                                    <DropdownMenuSubContent>
                                        {languages.map((lang) => (
                                            <DropdownMenuItem key={lang.code} onClick={() => changeLanguage(lang.code)}>
                                                <Flag code={lang.imageCode} className="h-4 w-4 mr-2"/>
                                                {lang.name}
                                            </DropdownMenuItem>
                                        ))}
                                    </DropdownMenuSubContent>
                                </DropdownMenuPortal>
                            </DropdownMenuSub>
                        </DropdownMenuGroup>
                        <DropdownMenuSeparator/>
                        <DropdownMenuItem onClick={logout}>
                            <LogOut/>
                            {t("header.logout")}
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </SidebarMenuItem>
        </SidebarMenu>
    )
}
