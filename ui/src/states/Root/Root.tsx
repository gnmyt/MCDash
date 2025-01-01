import {Sidebar} from "@/components/Sidebar.tsx"
import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList,
    BreadcrumbPage,
    BreadcrumbSeparator,
} from "@/components/ui/breadcrumb"
import {Separator} from "@/components/ui/separator"
import {
    SidebarInset,
    SidebarProvider,
    SidebarTrigger,
} from "@/components/ui/sidebar"
import {ServerInfoContext} from "@/contexts/ServerInfoContext.tsx";
import {useContext} from "react";
import {Navigate, Outlet, useLocation, useNavigate} from "react-router-dom";
import {getLocationByPath} from "@/states/Root/routes.tsx";

const Root = () => {
    const {tokenValid} = useContext(ServerInfoContext)!;

    const location = useLocation();
    const navigate = useNavigate();

    return (
        <>
            {tokenValid === false && <Navigate to="/login"/>}

            {tokenValid === true && <>
                <SidebarProvider>
                    <Sidebar/>
                    <SidebarInset>
                        <header className="flex h-16 shrink-0 items-center gap-2">
                            <div className="flex items-center gap-2 px-4">
                                <SidebarTrigger className="-ml-1"/>
                                <Separator orientation="vertical" className="mr-2 h-4"/>
                                <Breadcrumb>
                                    <BreadcrumbList>
                                        <BreadcrumbItem className="hidden md:block">
                                            <BreadcrumbLink className="cursor-pointer" onClick={() => navigate("/")}>Home</BreadcrumbLink>
                                        </BreadcrumbItem>
                                        <BreadcrumbSeparator className="hidden md:block"/>
                                        <BreadcrumbItem>
                                            <BreadcrumbPage>{getLocationByPath(location.pathname)?.name()}</BreadcrumbPage>
                                        </BreadcrumbItem>
                                    </BreadcrumbList>
                                </Breadcrumb>
                            </div>
                        </header>
                        <Outlet/>
                    </SidebarInset>
                </SidebarProvider>
            </>}
        </>
    )
}

export default Root;