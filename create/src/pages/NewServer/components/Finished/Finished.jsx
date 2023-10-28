import {Button, IconButton, Link, Stack, TextField, Typography} from "@mui/material";
import {Bolt, CopyAll, OpenInNew} from "@mui/icons-material";
import InstallationDialog from "./components/InstallationDialog";
import {useEffect, useState} from "react";
import {hashSync} from "bcryptjs";
import {getJavaVersion} from "../Server/versions.js";

const command_boilerplate = "curl -sSL https://create.mcdash.gnmyt.dev/install.sh | bash -s -- ";

export const Finished = ({software, password, memory, mcPort, panelPort, version, username, serverName}) => {
    const copyCommand = () => navigator.clipboard.writeText(generateCommand());

    const [open, setOpen] = useState(false);

    const [address, setAddress] = useState("");
    const [installationError, setInstallationError] = useState("");

    const generateCommand = () => {
        return command_boilerplate + [
            `\"${software}\"`,
            `\"${username + ": " + hashSync(password, 10).replace(/\$/g, "\\$")}\"`,
            `\"${Math.random().toString(36).substring(2, 7)}\"`,
            `\"${mcPort}\"`,
            `\"${panelPort}\"`,
            `\"${version}\"`,
            `\"${serverName}\"`,
            `"${getJavaVersion(version)}"`,
            `\"${parseInt(memory)*1024}\"`
        ].join(" ");
    }

    useEffect(() => {
        return () => {
            setInstallationError("");
            setAddress("");
        }
    }, []);

    return (
        <>
            <InstallationDialog open={open} setOpen={setOpen} command={generateCommand()} setAddress={setAddress}
                                setInstallationError={setInstallationError}/>

            {installationError !== "" && <Stack spacing={2} sx={{mt: 3}}>
                <Typography variant="body2" color="text.secondary" justifyContent="center" textAlign="center">
                    An error occurred while installing your server
                </Typography>

                <Typography variant="body2" color="text.secondary" justifyContent="center" textAlign="center">
                    {installationError}
                </Typography>

                <Button variant="contained" onClick={() => setOpen(true)}>Try again</Button>
            </Stack>}

            {address !== "" && <Stack justifyContent="center" textAlign="center">
                <Typography variant="body2" color="text.secondary">Your web panel is now accessible at</Typography>
                <Stack direction="row" alignItems="center" justifyContent="center">
                    <Link href={`http://${address}:${panelPort}`} target="_blank" rel="noopener noreferrer"
                          color="#ce93d8" underline="hover" variant="h6">{address}:{panelPort}</Link>
                    <IconButton href={`http://${address}:${panelPort}`} target="_blank" rel="noopener noreferrer">
                        <OpenInNew/>
                    </IconButton>
                </Stack>

                <Typography variant="body2" color="text.secondary">Or join the minecraft server</Typography>
                <Stack direction="row" alignItems="center" justifyContent="center">
                    <Typography color="primary" underline="hover" variant="h6">{address}:{mcPort}</Typography>
                    <IconButton onClick={() => navigator.clipboard.writeText(`${address}:${mcPort}`)}>
                        <CopyAll />
                    </IconButton>
                </Stack>
            </Stack>}

            {address === "" && installationError === "" && <Stack spacing={2} sx={{mt: 3}}>
                <TextField multiline fullWidth label="Run this command on your server" value={generateCommand()}
                           InputProps={{
                               readOnly: true, endAdornment: <IconButton onClick={copyCommand}><CopyAll/></IconButton>
                }}/>

                <Typography variant="body2" color="text.secondary" justifyContent="center" textAlign="center">
                    OR
                </Typography>

                <Stack spacing={1} justifyContent="center" textAlign="center">
                    <Button variant="contained" onClick={() => setOpen(true)}>Run directly on SSH Server</Button>
                    <Stack direction="row" alignItems="center" spacing={0.5} justifyContent="center">
                        <Bolt color="warning" fontSize={"small"}/>
                        <Typography variant="body2" color="text.secondary">This uses the</Typography>
                        <Link href="https://tools.gnmyt.dev/linux/ssh" target="_blank" rel="noopener noreferrer"
                              color="#ffa726" underline="hover" variant="body2">PowerTools API</Link>
                    </Stack>
                </Stack>
            </Stack>}
        </>
    )
}