import {IconButton, Stack, Typography} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {SchedulesContext} from "@/states/Root/pages/Scheduler/contexts/Schedules";
import React, {useContext, useState} from "react";
import mappings from "./mappings.jsx";
import EditActionDialog from "@/states/Root/pages/Scheduler/components/Action/components/EditActionDialog";
import backupMappings from "@/states/Root/pages/Backups/components/BackupItem/mappings.jsx";
import BackupCreationDialog from "@/states/Root/pages/Backups/components/BackupCreationDialog";

export const Action = ({name, type, payload, actions, index}) => {

    const [backupDialogOpen, setBackupDialogOpen] = useState(false);
    const [editDialogOpen, setEditDialogOpen] = useState(false);

    const {updateSchedules} = useContext(SchedulesContext);

    const editBackup = async (modes) => {
        if (modes.length === 0) return;

        const newActions = [...actions];
        newActions[index] = {type: "5", payload: modes.join("")};

        await patchRequest("schedules/actions", {name, actions: JSON.stringify(newActions)});
        updateSchedules();
        setBackupDialogOpen(false);
    }

    const deleteAction = async () => {
        const newActions = [...actions];
        newActions.splice(index, 1);

        await patchRequest("schedules/actions", {name, actions: JSON.stringify(newActions)});
        updateSchedules();
    }

    const openDialog = () => type === 5 ? setBackupDialogOpen(true) : setEditDialogOpen(true);

    return (
        <Stack backgroundColor="background.card" borderRadius={2} padding={2} justifyContent="space-between"
               sx={{mr: 1, mt: 1, width: "100%"}} direction="row" alignItems="center" gap={2}>

            <EditActionDialog open={editDialogOpen} setOpen={setEditDialogOpen} name={name} actions={actions}
                              payload={payload} actionType={type} index={index} />

            <BackupCreationDialog open={backupDialogOpen} setOpen={setBackupDialogOpen} name={name} actions={actions}
                                  actionMode createAction={editBackup} currentModes={payload?.split("")} />

            <Stack direction="row" alignItems="center" gap={2}>
                <Stack direction="row" alignItems="center" gap={1}>
                    {mappings()[type].icon}
                    <Typography variant="body1" fontWeight={500}>{mappings()[type].name}</Typography>
                </Stack>

                {type !== 5 && <Typography variant="body2" fontWeight={500} color="text.secondary">{payload}</Typography>}

                {type === 5 && <Stack direction="row" alignItems="center" gap={1}>
                    {payload?.split("").map((mode) => <Stack key={mode}>{backupMappings()[mode]}</Stack>)}
                </Stack>}
            </Stack>

            <Stack direction="row" alignItems="center" gap={1}>
                {!(type === 3 || type === 4) && <IconButton size="small" onClick={openDialog}><Edit/></IconButton>}
                <IconButton size="small" color="error" onClick={deleteAction}><Delete/></IconButton>
            </Stack>
        </Stack>
    );
}