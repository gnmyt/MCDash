import {Button, Stack, Typography} from "@mui/material";
import {PowerSettingsNew, Replay} from "@mui/icons-material";
import {request} from "@/common/utils/RequestUtil.js";
import {useState} from "react";
import ActionConfirmDialog from "@components/ActionConfirmDialog";

export const Overview = () => {
    const [shutdownOpen, setShutdownOpen] = useState(false);
    const [reloadOpen, setReloadOpen] = useState(false);

    const handleShutdown = async () => {
        return (await request("action/shutdown", "POST")).status === 200;
    };

    const handleReload = async () => {
        return (await request("action/reload", "POST")).status === 200;
    };

    return (
        <>
            <Typography variant="h5" fontWeight={500} sx={{mt: 2}}>Quick Control</Typography>

            <ActionConfirmDialog open={shutdownOpen} setOpen={setShutdownOpen} title="Shutdown server"
                                 description="Are you sure you want to shutdown the server?" buttonText="Yes, shutdown"
                                 onClick={handleShutdown} successMessage="Server successfully shutdown" />

            <ActionConfirmDialog open={reloadOpen} setOpen={setReloadOpen} title="Reload server"
                                 description="Are you sure you want to reload the server?" buttonText="Yes, reload"
                                 onClick={handleReload} successMessage="Server successfully reloaded" />


            <Stack spacing={2} direction="row" sx={{mt: 3}}>
                <Button variant="contained" color="error" startIcon={<PowerSettingsNew />}
                        onClick={() => setShutdownOpen(true)}>Shutdown</Button>

                <Button variant="contained" color="warning" startIcon={<Replay />}
                        onClick={() => setReloadOpen(true)}>Reload</Button>
            </Stack>
        </>
    )
}