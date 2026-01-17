import {
    CaretRightIcon,
    StorefrontIcon
} from "@phosphor-icons/react";

import {
    Sidebar as ShadSidebar,
    SidebarContent, SidebarFooter,
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
import {sidebar, getResourceIcon} from "@/states/Root/routes.tsx";
import {useContext, useState, useMemo} from "react";
import {Link, useLocation, useNavigate} from "react-router-dom";
import {ServerInfoContext} from "@/contexts/ServerInfoContext.tsx";
import {ResourcesContext} from "@/contexts/ResourcesContext.tsx";
import {UserProfile} from "@/components/UserProfile.tsx";
import {t} from "i18next";
import ServerImage from "@/assets/images/logo.png";

export function Sidebar() {
    const [openItems, setOpenItems] = useState<Record<string, boolean>>({});
    const {serverInfo} = useContext(ServerInfoContext)!;
    const resourcesContext = useContext(ResourcesContext);
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

    const capitalize = (str: string) => str.charAt(0).toUpperCase() + str.slice(1);

    const resourceSidebarItems = useMemo(() => {
        if (!resourcesContext || !serverInfo.availableFeatures?.includes("Resources")) {
            return [];
        }

        return resourcesContext.resourceTypes.map((resourceType) => {
            const TypeIcon = getResourceIcon(resourceType.identifier);
            const resources = resourcesContext.resourcesByType[resourceType.identifier] || [];
            
            return {
                path: `/resources/${resourceType.identifier}`,
                icon: TypeIcon,
                requiredFeatures: ["Resources"],
                name: () => t(`resources.types.${resourceType.identifier}`, capitalize(resourceType.identifier) + "s"),
                items: [
                    {
                        path: `/resources/${resourceType.identifier}/store`,
                        name: () => t("resources.store"),
                        icon: StorefrontIcon,
                        requiredFeatures: ["Resources"]
                    },
                    ...resources.map((resource) => ({
                        path: `/resources/${resourceType.identifier}/${encodeURIComponent(resource.fileName)}`,
                        name: () => resource.name,
                        icon: TypeIcon,
                        requiredFeatures: ["Resources"],
                        enabled: resource.enabled
                    }))
                ]
            };
        });
    }, [resourcesContext?.resourceTypes, resourcesContext?.resourcesByType, serverInfo.availableFeatures]);

    const allSidebarItems = useMemo(() => {
        const schedulesIndex = sidebar.findIndex(item => item.path === "/schedules");
        const insertIndex = schedulesIndex !== -1 ? schedulesIndex + 1 : sidebar.length - 1;
        
        const result = [...sidebar] as unknown[];
        result.splice(insertIndex, 0, ...resourceSidebarItems);
        return result as typeof sidebar;
    }, [resourceSidebarItems]);

    return (
        <ShadSidebar variant="inset" className="select-none">
            <SidebarHeader>
                <Link to="/" className="flex items-center gap-3 px-2 py-1 cursor-pointer">
                    <img src={ServerImage} alt="VoxelDash Logo" className="h-10 w-10"/>
                    <div className="grid flex-1 text-left leading-tight">
                        <span className="truncate font-bold text-lg">VoxelDash</span>
                        <span className="truncate text-xs text-muted-foreground">v1.2.0</span>
                    </div>
                </Link>
            </SidebarHeader>
            <SidebarContent>
                <SidebarGroup>
                    <SidebarMenu>
                        {allSidebarItems.map((item) => {
                            if (!isFeatureAvailable(item.requiredFeatures)) return null;

                            const visibleSubItems = item.items?.filter(subItem => isFeatureAvailable(subItem.requiredFeatures));

                            if (item.items && (!visibleSubItems || visibleSubItems.length === 0)) return null;

                            const isOpen = openItems[item.path] || false;

                            return (
                                <Collapsible key={item.path} asChild open={isOpen}>
                                    <SidebarMenuItem>
                                        <SidebarMenuButton
                                            asChild
                                            isActive={isCurrentRoute(item.path) && !visibleSubItems?.length}
                                            tooltip={item.name()}
                                            onClick={() => handleNavigationClick(item)}
                                            className="cursor-pointer">
                                            <a className="flex items-center gap-3">
                                                <item.icon weight={isCurrentRoute(item.path) ? "fill" : "regular"} />
                                                <span>{item.name()}</span>
                                            </a>
                                        </SidebarMenuButton>

                                        {visibleSubItems?.length ? (
                                            <>
                                                <CollapsibleTrigger asChild>
                                                    <SidebarMenuAction
                                                        className={`transition-transform duration-200 ${isOpen ? "rotate-90" : ""}`}
                                                        onClick={() => toggleCollapsible(item.path)}>
                                                        <CaretRightIcon/>
                                                        <span className="sr-only">Toggle</span>
                                                    </SidebarMenuAction>
                                                </CollapsibleTrigger>
                                                <CollapsibleContent>
                                                    <SidebarMenuSub>
                                                        {visibleSubItems.map((subItem: any) => (
                                                            <SidebarMenuSubItem key={subItem.path}>
                                                                <SidebarMenuSubButton
                                                                    asChild
                                                                    isActive={isCurrentRoute(subItem.path)}
                                                                    onClick={() => handleNavigationClick(subItem)}
                                                                    className={`cursor-pointer ${subItem.enabled === false ? "opacity-50" : ""}`}>
                                                                    <a className="flex items-center gap-3">
                                                                        <subItem.icon weight={isCurrentRoute(subItem.path) ? "fill" : "regular"} />
                                                                        <span className="flex-1">{subItem.name()}</span>
                                                                    </a>
                                                                </SidebarMenuSubButton>
                                                            </SidebarMenuSubItem>
                                                        ))}
                                                    </SidebarMenuSub>
                                                </CollapsibleContent>
                                            </>
                                        ) : null}
                                    </SidebarMenuItem>
                                </Collapsible>
                            );
                        })}
                    </SidebarMenu>
                </SidebarGroup>
            </SidebarContent>
            <SidebarFooter>
                <UserProfile/>
            </SidebarFooter>
        </ShadSidebar>
    );
}