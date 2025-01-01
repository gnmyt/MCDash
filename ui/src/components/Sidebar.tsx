import {
    ChevronRight,
    Cuboid
} from "lucide-react";

import {
    Sidebar as ShadSidebar,
    SidebarContent,
    SidebarGroup,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuAction,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarMenuSub,
    SidebarMenuSubButton,
    SidebarMenuSubItem,
} from "@/components/ui/sidebar";
import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "@/components/ui/collapsible.tsx";
import {sidebar} from "@/states/Root/routes.tsx";
import {useContext, useState} from "react";
import {Link, useLocation, useNavigate} from "react-router-dom";
import {ServerInfoContext} from "@/contexts/ServerInfoContext.tsx";

export function Sidebar() {
    const [openItems, setOpenItems] = useState<Record<string, boolean>>({});
    const {serverInfo} = useContext(ServerInfoContext)!;
    const navigate = useNavigate();
    const location = useLocation();

    const toggleCollapsible = (path: string) =>
        setOpenItems((prev) => ({...prev, [path]: !prev[path]}));

    const isFeatureAvailable = (requiredFeatures?: string[]) =>
        !requiredFeatures || requiredFeatures.every((feature) => serverInfo.availableFeatures?.includes(feature));

    const isCurrentRoute = (path: string) => {
        if (path !== "/" && location.pathname.startsWith(path)) return true;
        if (location.pathname === path) return true;
    }

    const handleNavigationClick = (item: { items?: { path: string; name: () => string }[]; path: string }) => {
        if (item.items?.length) {
            toggleCollapsible(item.path);
        }  else {
            navigate(item.path);
        }
    }

    return (
        <ShadSidebar variant="inset" className="select-none">
            <SidebarHeader>
                <SidebarMenu>
                    <SidebarMenuItem>
                        <SidebarMenuButton size="lg" asChild>
                            <Link to="/" className="flex items-center space-x-2 cursor-pointer">
                                <div
                                    className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                                    <Cuboid className="size-4"/>
                                </div>
                                <div className="grid flex-1 text-left text-sm leading-tight">
                                    <span className="truncate font-semibold">MCDash</span>
                                    <span className="truncate text-xs">1.2.0</span>
                                </div>
                            </Link>
                        </SidebarMenuButton>
                    </SidebarMenuItem>
                </SidebarMenu>
            </SidebarHeader>
            <SidebarContent>
                <SidebarGroup>
                    <SidebarMenu>
                        {sidebar.map((item) => {
                            if (!isFeatureAvailable(item.requiredFeatures)) return null;

                            const isOpen = openItems[item.path] || false;

                            return (
                                <Collapsible key={item.path} asChild open={isOpen}>
                                    <SidebarMenuItem>
                                        <SidebarMenuButton
                                            asChild
                                            isActive={isCurrentRoute(item.path) && !item.items?.length}
                                            tooltip={item.name()}
                                            onClick={() => handleNavigationClick(item)}
                                            className="cursor-pointer">
                                            <a className="flex items-center space-x-2">
                                                <item.icon/>
                                                <span>{item.name()}</span>
                                            </a>
                                        </SidebarMenuButton>

                                        {item.items?.length && (
                                            <>
                                                <CollapsibleTrigger asChild>
                                                    <SidebarMenuAction
                                                        className={`data-[state=${isOpen ? "open" : "closed"}]:rotate-90`}
                                                        onClick={() => toggleCollapsible(item.path)}>
                                                        <ChevronRight/>
                                                        <span className="sr-only">Toggle</span>
                                                    </SidebarMenuAction>
                                                </CollapsibleTrigger>
                                                <CollapsibleContent>
                                                    <SidebarMenuSub>
                                                        {item.items.map((subItem) => (
                                                            <SidebarMenuSubItem key={subItem.path}>
                                                                <SidebarMenuSubButton
                                                                    asChild
                                                                    isActive={isCurrentRoute(subItem.path)}
                                                                    onClick={() => handleNavigationClick(subItem)}
                                                                    className="cursor-pointer">
                                                                    <a>
                                                                        <subItem.icon/>
                                                                        <span>{subItem.name()}</span>
                                                                    </a>
                                                                </SidebarMenuSubButton>
                                                            </SidebarMenuSubItem>
                                                        ))}
                                                    </SidebarMenuSub>
                                                </CollapsibleContent>
                                            </>
                                        )}
                                    </SidebarMenuItem>
                                </Collapsible>
                            );
                        })}
                    </SidebarMenu>
                </SidebarGroup>
            </SidebarContent>
        </ShadSidebar>
    );
}