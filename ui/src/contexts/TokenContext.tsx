import { useState, createContext, useEffect, ReactNode } from "react";
import { request } from "@/lib/RequestUtil.ts";

interface TokenContextType {
    tokenValid: boolean | null;
    checkToken: () => Promise<boolean | undefined>;
    serverOnline: boolean | null;
}

export const TokenContext = createContext<TokenContextType | undefined>(undefined);

interface TokenProviderProps {
    children: ReactNode;
}

export const TokenProvider = (props: TokenProviderProps) => {
    const [tokenValid, setTokenValid] = useState<boolean | null>(null);
    const [serverOnline, setServerOnline] = useState<boolean | null>(null);

    const checkToken = async (): Promise<boolean | undefined> => {
        try {
            const r = await request("info");
            if (!r.ok && !(r.status === 400 || r.status === 401)) throw new Error("Server unavailable");
            setTokenValid(r.status === 200);
            setServerOnline(true);
            return r.status === 200;
        } catch {
            setServerOnline(false);
        }
    };

    useEffect(() => {
        checkToken();
        const interval = setInterval(() => checkToken(), 10000);
        return () => clearInterval(interval);
    }, []);

    return (
        <TokenContext.Provider value={{ tokenValid, checkToken, serverOnline }}>
            {props.children}
        </TokenContext.Provider>
    );
};
