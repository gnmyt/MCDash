import {Button, IconButton, Link, Stack, TextField, Typography} from "@mui/material";
import {Bolt, CopyAll} from "@mui/icons-material";
import InstallationDialog from "./components/InstallationDialog";
import {useState} from "react";
import bcrypt from "bcryptjs";

const command_boilerplate = "curl -sSL https://create.mcdash.gnmyt.dev/install.sh | bash -s -- ";

export const Finished = ({software, password, instanceId, mcPort, panelPort, version, username, serverName}) => {
    const copyCommand = () => navigator.clipboard.writeText(generateCommand());

    const [open, setOpen] = useState(false);

    const generateCommand = () => {
        return command_boilerplate + [
            `--software \"${software}\"`,
            `--user \"${username+":"+bcrypt.hashSync(password, 10)}\"`,
            `--instanceId \"${instanceId}\"`,
            `--mcPort \"${mcPort}\"`,
            `--panelPort \"${panelPort}\"`,
            `--version \"${version}\"`,
            `--serverName \"${serverName}\"`
        ].join(" ");
    }

    return (
        <>
            <InstallationDialog open={open} setOpen={setOpen}/>

            <Stack spacing={2} sx={{mt: 3}}>
                <TextField multiline fullWidth label="Run this command on your server" value={generateCommand()} InputProps={{
                    readOnly: true, endAdornment: <IconButton onClick={copyCommand}><CopyAll/></IconButton>}}/>

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
            </Stack>
        </>
    )
}