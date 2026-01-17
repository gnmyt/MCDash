import { useState, createContext, useEffect, ReactNode, useContext } from "react";
import { jsonRequest } from "@/lib/RequestUtil";
import { Resource, ResourceType } from "@/types/resource";
import { ServerInfoContext } from "./ServerInfoContext";

interface ResourcesContextType {
    resourceTypes: ResourceType[];
    resourcesByType: Record<string, Resource[]>;
    loading: boolean;
    refreshResources: (type?: string) => Promise<void>;
}

export const ResourcesContext = createContext<ResourcesContextType | undefined>(undefined);

interface ResourcesProviderProps {
    children: ReactNode;
}

export const ResourcesProvider = (props: ResourcesProviderProps) => {
    const serverInfoContext = useContext(ServerInfoContext);
    const [resourceTypes, setResourceTypes] = useState<ResourceType[]>([]);
    const [resourcesByType, setResourcesByType] = useState<Record<string, Resource[]>>({});
    const [loading, setLoading] = useState(true);

    const fetchResourcesForType = async (type: string): Promise<Resource[]> => {
        try {
            const data = await jsonRequest(`resources/list?type=${type}`);
            return data.resources || [];
        } catch (error) {
            console.error(`Failed to fetch resources for type ${type}:`, error);
            return [];
        }
    };

    const refreshResources = async (specificType?: string) => {
        if (!serverInfoContext?.serverInfo?.availableFeatures?.includes("Resources")) {
            return;
        }

        const types = serverInfoContext?.serverInfo?.resourceTypes || [];
        
        if (specificType) {
            const resources = await fetchResourcesForType(specificType);
            setResourcesByType(prev => ({
                ...prev,
                [specificType]: resources
            }));
        } else {
            const allResources: Record<string, Resource[]> = {};
            for (const type of types) {
                allResources[type.identifier] = await fetchResourcesForType(type.identifier);
            }
            setResourcesByType(allResources);
        }
    };

    useEffect(() => {
        const loadResources = async () => {
            if (!serverInfoContext?.serverInfo?.availableFeatures?.includes("Resources")) {
                setLoading(false);
                return;
            }

            const types = serverInfoContext?.serverInfo?.resourceTypes || [];
            setResourceTypes(types);

            const allResources: Record<string, Resource[]> = {};
            for (const type of types) {
                allResources[type.identifier] = await fetchResourcesForType(type.identifier);
            }
            setResourcesByType(allResources);
            setLoading(false);
        };

        if (serverInfoContext?.tokenValid) {
            loadResources();
        }
    }, [serverInfoContext?.serverInfo?.resourceTypes, serverInfoContext?.tokenValid]);

    return (
        <ResourcesContext.Provider value={{ resourceTypes, resourcesByType, loading, refreshResources }}>
            {props.children}
        </ResourcesContext.Provider>
    );
};
