import {createContext, useEffect, useState} from "react";
import {socket} from "./socket.js";

export const SocketContext = createContext({});

export const SocketProvider = ({children}) => {

    const [loginSuccess, setLoginSuccess] = useState(null);
    const [commands, setCommands] = useState([]);

    const connect = ({hostname, port = 22, username, password}) => {
        if (socket.connected) return;

        socket.connect();

        socket.on("type", () => {
            socket.emit("login", {port, host: hostname, username, password});
        });
    }

    const sendCommand = (command) => {
        if (!socket.connected) return;

        socket.emit("command", command + "\n");
    }

    const disconnect = () => {
        if (socket.connected) socket.disconnect();
        socket.off("type");
        setCommands([]);
    }

    useEffect(() => {
        const onConnect = () => socket.emit("type", "ssh");

        const onLogin = ({status}) => {
            if (status !== "success") {
                setLoginSuccess(false);
                return disconnect();
            }
            setLoginSuccess(true);
        }

        const onCommand = ({data}) => {
            if (!data) return;
            if (!data.toString().includes("MCDash | ")) return;
            if (data.toString().includes("\n")) return data.toString().split("\n").forEach(line => onCommand({data: line}));

            setCommands(prevCommands => [...prevCommands, data.toString().replace("MCDash | ", "")]);
        }

        socket.on("connect", onConnect);
        socket.on("login", onLogin);
        socket.on("command", onCommand);

        return () => {
            socket.off("connect", onConnect);
            socket.off("login", onLogin);
            socket.off("command", onCommand);

            if (socket.connected) disconnect();
        }
    }, []);


    return (
        <SocketContext.Provider value={{loginSuccess, connect, commands, setLoginSuccess, sendCommand, disconnect}}>
            {children}
        </SocketContext.Provider>
    )
}