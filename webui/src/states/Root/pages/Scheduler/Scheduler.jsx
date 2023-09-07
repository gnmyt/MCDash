import {Button, Stack, Typography} from "@mui/material";
import {t} from "i18next";
import {Add} from "@mui/icons-material";
import React, {useContext, useState} from "react";
import {SchedulesContext} from "@/states/Root/pages/Scheduler/contexts/Schedules";
import Schedule from "@/states/Root/pages/Scheduler/components/Schedule";
import CreateScheduleDialog from "@/states/Root/pages/Scheduler/components/CreateScheduleDialog";

export const Scheduler = () => {

    const {schedules} = useContext(SchedulesContext);
    const [dialogOpen, setDialogOpen] = useState(false);

    return (
        <Stack gap={2} sx={{mt: 2, mb: 2}}>
            <CreateScheduleDialog open={dialogOpen} setOpen={setDialogOpen}/>
            <Stack direction="row" alignItems="center" justifyContent="space-between">
                <Typography variant="h5" fontWeight={500}>{t("nav.schedules")}</Typography>
                <Button variant="outlined" color="secondary" startIcon={<Add/>}
                        onClick={() => setDialogOpen(true)}>{t("schedules.create")}</Button>
            </Stack>

            <Stack alignItems="flex-start" gap={1} direction={{sm: "column", lg: "row"}}>
                {schedules.length === 0 && <Typography width="100%" textAlign="center">{t("schedules.none_created")}</Typography>}
                {schedules.length !== 0 && <Stack direction="column" gap={1} sx={{width: {sm: "100%", lg: "48%"}}}>
                    {schedules.slice(0, Math.ceil(schedules.length / 2)).map((schedule) => (
                        <Schedule key={schedule.name} {...schedule}/>))}
                </Stack>}
                {schedules.length !== 0 && <Stack direction="column" gap={1} sx={{width: {sm: "100%", lg: "48%"}}}>
                    {schedules.slice(Math.ceil(schedules.length / 2)).map((schedule) => (
                        <Schedule key={schedule.name} {...schedule}/>))}
                </Stack>}
            </Stack>
        </Stack>
    )
}