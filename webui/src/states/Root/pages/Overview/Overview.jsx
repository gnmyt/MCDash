import {Box, Button, Stack, Typography} from "@mui/material";
import {PowerSettingsNew, Replay} from "@mui/icons-material";
import {request} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import ActionConfirmDialog from "@components/ActionConfirmDialog";
import {StatsContext} from "@/states/Root/pages/Overview/contexts/StatsContext";
import StatisticBox from "@/states/Root/pages/Overview/components/StatisticBox";

export const Overview = () => {
    const [shutdownOpen, setShutdownOpen] = useState(false);
    const [reloadOpen, setReloadOpen] = useState( false);
    const {stats} = useContext(StatsContext);

    const handleShutdown = async () => {
        return (await request("action/shutdown", "POST")).status === 200;
    };

    const handleReload = async () => {
        return (await request("action/reload", "POST")).status === 200;
    };

    return (
        <>
            <Typography variant="h5" fontWeight={500}>Quick Overview</Typography>

            <Stack direction="row" sx={{mt: 3, flexDirection: {xs: "column", lg: "row"}}} gap={2}>
                <StatisticBox title="CPU-Cores" value={stats.processors}/>

                <StatisticBox title="TPS" value={stats.tps}/>

                <StatisticBox title="RAM" value={`${(stats.used_memory / 1024 / 1024 / 1024)
                    .toFixed(2)} / ${(stats.total_memory / 1024 / 1024 / 1024).toFixed(2)} GB`}/>

                <StatisticBox title="Disk" value={`${(stats.used_space / 1024 / 1024 / 1024)
                    .toFixed(2)} / ${(stats.total_space / 1024 / 1024 / 1024).toFixed(2)} GB`}/>
            </Stack>

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