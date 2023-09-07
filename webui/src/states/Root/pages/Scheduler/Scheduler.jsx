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

            <Stack direction="column" gap={1}>
                {schedules.map((schedule) => <Schedule key={schedule.name} {...schedule}/>)}
                {schedules.length === 0 && <Typography textAlign="center">{t("backup.none_found")}</Typography>}
            </Stack>
        </Stack>
    )
}