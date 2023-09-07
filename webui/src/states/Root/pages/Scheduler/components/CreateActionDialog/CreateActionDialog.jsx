import {Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Select, Stack, TextField} from "@mui/material";
import {SchedulesContext} from "@/states/Root/pages/Scheduler/contexts/Schedules";
import {useContext, useEffect, useState} from "react";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {t} from "i18next";
import mappings from "../Action/mappings.jsx";

export const CreateActionDialog = ({open, setOpen, name, actions}) => {

    const {updateSchedules} = useContext(SchedulesContext);

    const [actionType, setActionType] = useState("1");
    const [payload, setPayload] = useState("");

    const createAction = async (event) => {
        event.preventDefault();
        if (!actionType) return;

        if ((actionType === "1" || actionType === "2" || actionType === "6") && !payload) return;

        const newActions = [...actions, {type: actionType, payload: actionType === "5" ? "0" : payload}];
        await patchRequest("schedules/actions", {name, actions: JSON.stringify(newActions)});

        updateSchedules();
        setOpen(false);
    }

    useEffect(() => {
        setActionType("1");
        setPayload("");
    }, [open]);

    return (
        <Dialog open={open} onClose={() => setOpen(false)} component="form" onSubmit={createAction}>
            <DialogTitle>{t("schedules.action.create")}</DialogTitle>
            <DialogContent>
                <Stack direction="column" gap={2}>
                    <Select value={actionType} onChange={(e) => setActionType(e.target.value)} fullWidth>
                        {Object.keys(mappings()).map((key) => (
                            <MenuItem key={key} value={key}>
                                <Stack direction="row" gap={2}>
                                    {mappings()[key].icon}
                                    {mappings()[key].name}
                                </Stack>
                            </MenuItem>
                        ))}
                    </Select>
                    {(actionType === "1" || actionType === "2" || actionType === "6") &&
                        <TextField value={payload} onChange={(e) => setPayload(e.target.value)} fullWidth
                                   label={t("schedules.action." + (actionType === "1" ? "command" : actionType === "2" ? "message" : "kick"))}/>}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                <Button type="submit">{t("action.create")}</Button>
            </DialogActions>
        </Dialog>
    )
}