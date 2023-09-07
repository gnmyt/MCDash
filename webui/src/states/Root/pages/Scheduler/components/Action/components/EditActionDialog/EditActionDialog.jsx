import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField} from "@mui/material";
import {SchedulesContext} from "@/states/Root/pages/Scheduler/contexts/Schedules";
import {useContext, useState} from "react";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {t} from "i18next";

export const EditActionDialog = ({open, setOpen, actionType, payload, index, actions, name}) => {

    const {updateSchedules} = useContext(SchedulesContext);

    const [newPayload, setPayload] = useState(payload);

    const editAction = async (event) => {
        event.preventDefault();

        const newActions = [...actions];
        newActions[index] = {type: actionType, payload: newPayload};

        await patchRequest("schedules/actions", {name, actions: JSON.stringify(newActions)});

        updateSchedules();
        setOpen(false);
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)} component="form" onSubmit={editAction}>
            <DialogTitle>{t("schedules.action.edit")}</DialogTitle>
            <DialogContent>
                <Stack direction="column" gap={2} mt={1}>
                    {(actionType === 1 || actionType === 2 || actionType === 6) &&
                        <TextField value={newPayload} onChange={(e) => setPayload(e.target.value)} fullWidth
                                   label={t("schedules.action." + (actionType === 1 ? "command" : actionType === 2 ? "message" : "kick"))}/>}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                <Button type="submit" color="primary">{t("action.save")}</Button>
            </DialogActions>
        </Dialog>
    );
}