import {createContext, ReactNode, useContext} from "react";
import {useState, useEffect} from 'react';
import useWebSocket, {ReadyState} from 'react-use-websocket';
import {ServerInfoContext} from "@/contexts/ServerInfoContext.tsx";

interface SocketContextType {
    attachEventListener: (eventName: string) => void;
    detachEventListener: (eventName: string) => void;
    lastMessage: MessageEvent | null;
    readyState: ReadyState;
}

export const SocketContext = createContext<SocketContextType | undefined>(undefined);

interface SocketProps {
    children: ReactNode;
}

export const SocketProvider = (props: SocketProps) => {
    const {serverInfo} = useContext(ServerInfoContext)!;

    const [socketUrl, setSocketUrl] = useState<string | null>(null);
    const {sendMessage, lastMessage, readyState} = useWebSocket(socketUrl);

    const attachEventListener = (eventName: string) => {
        sendMessage(JSON.stringify({event: 'ATTACH', name: eventName}));
    }

    const detachEventListener = (eventName: string) => {
        sendMessage(JSON.stringify({event: 'DETACH', name: eventName}));
    }

    useEffect(() => {
        const sessionToken = localStorage.getItem('sessionToken');

        if (serverInfo) {
            const hostname = window.location.hostname;
            const port = window.location.port;
            const isSSL = window.location.protocol === 'https:';

            setSocketUrl(`${isSSL ? 'wss' : 'ws'}://${hostname}:${port}/api/ws?sessionToken=${sessionToken}`);
        }
    }, [serverInfo]);

    return (
        <SocketContext.Provider value={{attachEventListener, detachEventListener, lastMessage, readyState}}>
            {props.children}
        </SocketContext.Provider>
    );
};
