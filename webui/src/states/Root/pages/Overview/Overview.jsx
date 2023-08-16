import {Button, Stack, Typography} from "@mui/material";
import {PowerSettingsNew, Replay} from "@mui/icons-material";
import {request} from "@/common/utils/RequestUtil.js";
import {useContext, useState} from "react";
import ActionConfirmDialog from "@components/ActionConfirmDialog";
import {StatsContext} from "@/states/Root/pages/Overview/contexts/StatsContext";
import StatisticBox from "@/states/Root/pages/Overview/components/StatisticBox";
import {t} from "i18next";

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
            <Typography variant="h5" fontWeight={500}>{t("nav.overview")}</Typography>

            <Stack direction="row" sx={{mt: 3, flexDirection: {xs: "column", lg: "row"}}} gap={2}>
                <StatisticBox title={t("overview.cpu")} value={stats.processors}/>

                <StatisticBox title={t("overview.tps")} value={stats.tps}/>

                <StatisticBox title={t("overview.ram")} value={`${(stats.used_memory / 1024 / 1024 / 1024)
                    .toFixed(2)} / ${(stats.total_memory / 1024 / 1024 / 1024).toFixed(2)} GB`}/>

                <StatisticBox title={t("overview.disk")} value={`${(stats.used_space / 1024 / 1024 / 1024)
                    .toFixed(2)} / ${(stats.total_space / 1024 / 1024 / 1024).toFixed(2)} GB`}/>
            </Stack>

            <Typography variant="h5" fontWeight={500} sx={{mt: 2}}>{t("overview.control")}</Typography>

            <ActionConfirmDialog open={shutdownOpen} setOpen={setShutdownOpen} title={t("overview.shutdown.title")}
                                 description={t("overview.shutdown.text")} buttonText={t("overview.shutdown.yes")}
                                 onClick={handleShutdown} successMessage={t("overview.shutdown.success")} />

            <ActionConfirmDialog open={reloadOpen} setOpen={setReloadOpen} title={t("overview.reload.title")}
                                 description={t("overview.reload.text")} buttonText={t("overview.reload.yes")}
                                 onClick={handleReload} successMessage={t("overview.reload.success")} />


            <Stack spacing={2} direction="row" sx={{mt: 3}}>
                <Button variant="contained" color="error" startIcon={<PowerSettingsNew />}
                        onClick={() => setShutdownOpen(true)}>{t("overview.shutdown.button")}</Button>

                <Button variant="contained" color="warning" startIcon={<Replay />}
                        onClick={() => setReloadOpen(true)}>{t("overview.reload.button")}</Button>
            </Stack>
        </>
    )
}