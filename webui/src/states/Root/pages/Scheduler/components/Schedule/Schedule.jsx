import {Box, Button, Collapse, Stack, Typography} from "@mui/material";
import {AddTask, CalendarMonth, Delete, ExpandMore} from "@mui/icons-material";
import {useContext, useState} from "react";
import Action from "@/states/Root/pages/Scheduler/components/Action";
import CreateActionDialog from "@/states/Root/pages/Scheduler/components/CreateActionDialog";
import {deleteRequest} from "@/common/utils/RequestUtil.js";
import {SchedulesContext} from "@/states/Root/pages/Scheduler/contexts/Schedules";
import ActionConfirmDialog from "@components/ActionConfirmDialog";
import {t} from "i18next";

export const Schedule = ({name, execution, actions}) => {
    const [open, setOpen] = useState(true);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

    const {updateSchedules} = useContext(SchedulesContext);

    const deleteSchedule = async () => {
        deleteRequest("schedules/", {name})
            .then(() => updateSchedules());

        setDeleteDialogOpen(false);
        return true;
    }

    return (
        <Box backgroundColor="background.darker" borderRadius={2} padding={2} sx={{mr: 1, mt: 1, width: "100%"}}>
            <CreateActionDialog open={dialogOpen} setOpen={setDialogOpen} actions={actions} name={name}/>

            <ActionConfirmDialog open={deleteDialogOpen} setOpen={setDeleteDialogOpen} onClick={deleteSchedule}
                                 successMessage="none"
                                 title={t("schedules.delete.title")} description={t("schedules.delete.text")}/>

            <Stack direction="row" justifyContent="space-between" alignItems="center" onClick={() => setOpen(!open)}
                   style={{cursor: "pointer"}}>
                <Stack direction="row" alignItems="center" gap={2}>
                    <CalendarMonth fontSize="medium" color="secondary"/>

                    <Stack direction="column" gap={0}>
                        <Typography variant="body1" fontWeight={500}>{name}</Typography>
                        <Typography variant="body2" fontWeight={500}
                                    color="text.secondary">{execution.frequency} {execution.time}</Typography>
                    </Stack>
                </Stack>

                <ExpandMore sx={{transform: open ? "rotate(180deg)" : "rotate(0deg)", transition: "0.3s"}}/>
            </Stack>

            <Collapse in={open}>
                <Stack direction="row" gap={2} sx={{mt: 2}} alignItems="center" justifyContent="space-between">

                    {actions.length !== 0 && <Typography variant="body2" fontWeight={500} color="text.secondary">
                        {t("schedules.tasks.title")} ({actions.length})</Typography>}
                    {actions.length === 0 && <Typography variant="body2" fontWeight={500} color="text.secondary">
                        {t("schedules.tasks.none_created")}</Typography>}

                    <Stack direction="row" gap={1} alignItems="center">
                        <Button variant="outlined" color="error" size="small" startIcon={<Delete/>}
                                onClick={() => setDeleteDialogOpen(true)}>{t("schedules.tasks.delete")}</Button>
                        <Button variant="outlined" color="secondary" size="small" startIcon={<AddTask/>}
                                onClick={() => setDialogOpen(true)}>{t("schedules.tasks.add")}</Button>
                    </Stack>
                </Stack>

                <Stack direction="column" gap={1} sx={{mt: 1}}>
                    {actions.map((action, index) => <Action name={name} key={index} actions={actions}
                                                            index={index} {...action}/>)}
                </Stack>
            </Collapse>
        </Box>
    );
}