import {useContext, useEffect, useState} from "react";
import {
    Alert,
    Box,
    Button,
    Checkbox,
    CircularProgress,
    FormControlLabel,
    Stack,
    TextField,
    Typography
} from "@mui/material";
import {Download, Search} from "@mui/icons-material";
import {SocketContext} from "@/common/contexts/SocketContext/index.js";

const getCommand = (path) => `curl -sSL https://create.mcdash.gnmyt.dev/plugin.sh | bash -s -- \"${path}\"\n`;

export const PluginInstaller = ({setOpen, setInstalled}) => {

    const {loginSuccess, connect, commands, setLoginSuccess, sendCommand, disconnect} = useContext(SocketContext);

    const [hostname, setHostname] = useState("");
    const [port, setPort] = useState(22);

    const [username, setUsername] = useState("root");
    const [password, setPassword] = useState("");

    const [loginFailed, setLoginFailed] = useState(false);

    const [selectedFolder, setSelectedFolder] = useState(null);

    const [folders, setFolders] = useState([]);
    const [finished, setFinished] = useState(false);

    const [installing, setInstalling] = useState(false);

    useEffect(() => {

        const folders = [];
        for (const command of commands) {
            if (command.includes("[SERVER]"))
                folders.push(command.split("[SERVER]")[1].split("[/SERVER]")[0]);
        }

        setFolders(folders);

        const latestCommand = commands[commands.length - 1];
        if (!latestCommand) return;

        if (latestCommand.includes("[DONE]")) setFinished(true);

        if (latestCommand.includes("[IDONE]")) {
            setInstalled(true);
            setOpen(false);
            disconnectSocket();
        }
    }, [commands]);

    useEffect(() => {
        if (loginSuccess === false) {
            setLoginFailed(true);
            setLoginSuccess(null);
        }

        if (loginSuccess) {
            setLoginFailed(false);
            setTimeout(() => sendCommand("curl -sSL https://create.mcdash.gnmyt.dev/lookup.sh | bash\n"), 2000);
        }
    }, [loginSuccess]);

    const lookup = () => {
        connect({hostname, port, username, password});
        sendCommand("whoami");
    }

    const disconnectSocket = () => {
        disconnect();
        setLoginSuccess(null);
        setLoginFailed(false);
        setFolders([]);
        setFinished(false);
        setInstalling(false);
    }

    const install = () => {
        if (installing || !selectedFolder) return;
        setInstalling(true);
        sendCommand(getCommand(selectedFolder));
    }

    return (
        <Stack direction="column" justifyContent="space-between" spacing={2}>

            {loginSuccess && (
                <>
                    <Typography variant="body2" color="text.secondary" justifyContent="center" textAlign="center">
                        {finished && folders.length === 0 ? "This server does not have any minecraft servers"
                            : (finished ? "Select the folder you want to install the plugin to" : "Loading folders...")}

                    </Typography>

                    {folders.map(folder => (
                        <FormControlLabel key={folder} control={<Checkbox checked={selectedFolder === folder}
                                                                          onChange={() => setSelectedFolder(folder)}/>}
                                          label={folder}/>
                    ))}
                </>
            )}

            {loginSuccess === null && (
                <>
                    {loginFailed && <Alert severity="error">Failed to connect to server</Alert>}

                    <Typography variant="body2" color="text.secondary" justifyContent="center" textAlign="center">
                        Enter your server's SSH details
                    </Typography>

                    <Stack spacing={1} sx={{mt: 2}} direction="row" alignItems="center">
                        <TextField label="IP Address" sx={{width: "80%"}} value={hostname}
                                   onChange={e => setHostname(e.target.value)}/>
                        <Typography variant="body2" color="text.secondary">:</Typography>
                        <TextField label="Port" sx={{width: "20%"}} type="number" value={port}
                                   onChange={e => setPort(e.target.value)}/>
                    </Stack>

                    <Stack spacing={1} sx={{mt: 2}} direction="row" alignItems="center">
                        <TextField label="Username" value={username}
                                   onChange={e => setUsername(e.target.value)} autoComplete="off"/>
                        <TextField label="Password" type="password" value={password}
                                   onChange={e => setPassword(e.target.value)} autoComplete="off"/>
                    </Stack>
                </>)}

            <Box sx={{display: 'flex', flexDirection: 'row'}}>
                <Button color="inherit" onClick={loginSuccess ? disconnectSocket : () => setOpen(false)} sx={{mr: 1}}>
                    {loginSuccess ? "Disconnect" : "Back"}
                </Button>

                <Box sx={{flex: '1 1 auto'}}/>

                {installing && <CircularProgress sx={{mr: 1}}/>}
                {!installing && <Button variant="contained" startIcon={loginSuccess ? <Download /> : <Search/>}
                                        onClick={loginSuccess ? install : lookup}>
                    {loginSuccess ? "Install" : "Lookup"}
                </Button>}
            </Box>
        </Stack>
    )
}