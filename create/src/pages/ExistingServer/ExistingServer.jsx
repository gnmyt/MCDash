import {Alert, Button, IconButton, Link, Stack, TextField, Typography} from "@mui/material";
import {Bolt, CopyAll} from "@mui/icons-material";
import PluginInstaller from "@/pages/ExistingServer/components/PluginInstaller";
import {useState} from "react";

const command = "curl -sL -o /tmp/mcdash.sh https://create.mcdash.gnmyt.dev/plugin.sh && bash /tmp/mcdash.sh";

export const ExistingServer = () => {
    const copy = () => navigator.clipboard.writeText(command);

    const [open, setOpen] = useState(false);

    const [installed, setInstalled] = useState(false);

    return (
        <>
            {installed && <>
                <Alert severity="success" sx={{mb: 2}}>The plugin has been installed successfully!</Alert>

                <Button variant="contained" onClick={() => setInstalled(false)} fullWidth>Install on another server</Button>
            </>}
            {!installed && open && <PluginInstaller setOpen={setOpen} setInstalled={setInstalled}/>}
            {!installed && !open && <Stack direction="column" justifyContent="space-between" spacing={2} sx={{mt: 3}}>
                <TextField fullWidth label="Run this command on your server" defaultValue={command} InputProps={{
                    readOnly: true,
                    endAdornment: <IconButton onClick={copy}><CopyAll/></IconButton>
                }}/>

                <Typography variant="body2" color="text.secondary" justifyContent="center" textAlign="center">
                    OR
                </Typography>

                <Stack spacing={1} justifyContent="center" textAlign="center">
                    <Button variant="contained" onClick={() => setOpen(true)}>Lookup installed servers</Button>
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