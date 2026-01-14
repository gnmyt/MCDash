import { useState, createContext, useEffect, ReactNode } from "react";
import { request } from "@/lib/RequestUtil.ts";
import { ResourceType } from "@/types/resource";

interface ServerInfo {
    accountName?: string;
    serverSoftware?: string;
    serverVersion?: string;
    serverPort?: number;
    availableFeatures?: string[];
    resourceTypes?: ResourceType[];
    isAdmin?: boolean;
}

interface ServerInfoContextType {
    tokenValid: boolean | null;
    checkToken: () => Promise<boolean | undefined>;
    serverInfo: ServerInfo;
}

export const ServerInfoContext = createContext<ServerInfoContextType | undefined>(undefined);

interface ServerInfoProviderProps {
    children: ReactNode;
}

export const ServerInfoProvider = (props: ServerInfoProviderProps) => {
    const [tokenValid, setTokenValid] = useState<boolean | null>(null);
    const [serverInfo, setServerInfo] = useState<ServerInfo>({});

    const checkToken = async (): Promise<boolean | undefined> => {
        try {
            const r = await request("info");
            if (!r.ok && !(r.status === 400 || r.status === 401)) throw new Error("Server unavailable");
            setTokenValid(r.status === 200);
            setServerInfo(await r.json());
            return r.status === 200;
        } catch {
            setServerInfo({});
        }
    };

    useEffect(() => {
        checkToken();
    }, []);

    return (
        <ServerInfoContext.Provider value={{ tokenValid, checkToken, serverInfo }}>
            {props.children}
        </ServerInfoContext.Provider>
    );
};
