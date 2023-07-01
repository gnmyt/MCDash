import {
    Alert,
    Box,
    Button,
    CircularProgress,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Stack,
    TextField,
    Typography
} from "@mui/material";
import {useContext, useEffect, useState} from "react";
import {SocketContext} from "@/common/contexts/SocketContext";

export const InstallationDialog = ({open, setOpen, command, setAddress, setInstallationError}) => {

    const {loginSuccess, connect, commands, setLoginSuccess, sendCommand, disconnect} = useContext(SocketContext);

    const [hostname, setHostname] = useState("");
    const [port, setPort] = useState(22);
    const [username, setUsername] = useState("root");
    const [password, setPassword] = useState("");

    const [loginFailed, setLoginFailed] = useState(false);

    const [processing, setProcessing] = useState(false);
    const [installationRunning, setInstallationRunning] = useState(false);

    const [progress, setProgress] = useState(0);

    useEffect(() => {
        const currentCommand = commands[commands.length - 1]?.toString();

        if (currentCommand === undefined) return;

        if (currentCommand.includes("Error")) {
            setProgress(0);
            setInstallationRunning(false);
            setProcessing(false);
            setLoginSuccess(null);
            disconnect();
            setInstallationError(currentCommand.substring(currentCommand.indexOf(">") + 1,
                currentCommand.lastIndexOf("<")));
            setOpen(false);
        }

        if (currentCommand.startsWith("parameter check")) setProgress(20);
        if (currentCommand.startsWith("dependencies")) setProgress(30);
        if (currentCommand.startsWith("installation")) setProgress(40);
        if (currentCommand.startsWith("configuration")) setProgress(70);
        if (currentCommand.startsWith("plugin")) setProgress(80);
        if (currentCommand.startsWith("service")) setProgress(90);
        if (currentCommand.startsWith("waiting")) setProgress(0);

        if (currentCommand.startsWith("finished")) {
            setProgress(100);
            setInstallationRunning(false);
            disconnect();
            setAddress(hostname);
            setOpen(false);
        }
    }, [commands]);

    useEffect(() => {
        if (loginSuccess === null) return;
        if (installationRunning) return;

        if (loginSuccess) {
            setProgress(10);
            setInstallationRunning(true);
            setTimeout(() => sendCommand(command), 3000);
        } else {
            setProcessing(false);
            setLoginFailed(true);
            setLoginSuccess(null);
        }
    }, [loginSuccess]);

    const onClose = () => {
        if (processing) return;

        setOpen(false);
        setLoginFailed(false);
    }

    const install = () => {
        setProcessing(true);

        connect({hostname, port, username, password});
    }


    return (
        <Dialog open={open} onClose={onClose} aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description">
            <Box component="form" sx={{display: "flex", flexDirection: "column"}}>
                <DialogTitle id="alert-dialog-title">
                    {processing ? "Installing" : "Login"}
                </DialogTitle>
                {processing && <DialogContent sx={{display: "flex", justifyContent: "center"}}>
                    <CircularProgress value={progress} variant={progress === 0 ? "indeterminate" : "determinate"}/>
                </DialogContent>}
                {!processing && <DialogContent sx={{maxWidth: "25rem"}}>
                    <DialogContentText id="alert-dialog-description">
                        Please enter the login credentials for your server.
                    </DialogContentText>

                    {loginFailed && <Alert severity="error">Please check your login credentials</Alert>}

                    <Stack spacing={1} sx={{mt: 2}} direction="row" alignItems="center">
                        <TextField label="IP Address" sx={{width: "80%"}} size="small" value={hostname}
                                   onChange={e => setHostname(e.target.value)}/>
                        <Typography variant="body2" color="text.secondary">:</Typography>
                        <TextField label="Port" sx={{width: "20%"}} size="small" type="number" value={port}
                                   onChange={e => setPort(e.target.value)}/>
                    </Stack>

                    <Stack spacing={1} sx={{mt: 2}} direction="row" alignItems="center">
                        <TextField label="Username" size="small" value={username}
                                   onChange={e => setUsername(e.target.value)} autoComplete="off" />
                        <TextField label="Password" size="small" type="password" value={password}
                                   onChange={e => setPassword(e.target.value)} autoComplete="off"/>
                    </Stack>
                </DialogContent>}
                {!processing &&
                    <DialogActions>
                        <Button onClick={onClose}>Cancel</Button>
                        <Button onClick={install} autoFocus>Install</Button>
                    </DialogActions>}
            </Box>
        </Dialog>
    )
}