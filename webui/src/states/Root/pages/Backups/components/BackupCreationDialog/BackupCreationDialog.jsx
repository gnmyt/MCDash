import {
    Alert,
    Box,
    Button,
    Checkbox,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    FormControlLabel, Snackbar,
    Stack
} from "@mui/material";
import React, {useContext, useEffect, useState} from "react";
import {request} from "@/common/utils/RequestUtil.js";
import {BackupContext} from "@/states/Root/pages/Backups/contexts/Backups";
import {t} from "i18next";

export const BackupCreationDialog = ({open, setOpen, setLoading, currentModes = [], actionMode = false, createAction}) => {

    const {updateBackups} = useContext(BackupContext);

    const [modes, setModes] = useState([]);
    const [finished, setFinished] = useState(false);

    const toggleMode = (mode) => {
        if (!modes.includes(mode)) {
            if (mode === "0") return setModes(["0"]);
            setModes(modes => [...modes, mode]);
        } else {
            setModes(modes => modes.filter(m => m !== mode));
        }
    }

    const executeAction = () => {
        if (modes.length === 0) return;
        setOpen(false);
        setLoading(true);
        request("backups/", "PUT", {mode: modes.join("")}, {}, false).then(() => {
            setFinished(true);
            setLoading(false);
            updateBackups();
        });
    }

    useEffect(() => {
      setModes(currentModes || []);
    }, [open]);

    return (
        <>
            <Snackbar open={finished} autoHideDuration={5000} onClose={() => setFinished(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setFinished(false)} severity="success" sx={{width: '100%'}}>
                    {t("backup.created")}
                </Alert>
            </Snackbar>

            <Dialog open={open} onClose={() => setOpen(false)}>
                <Box component="form" noValidate>
                    <DialogTitle>{actionMode ? t("schedules.action.edit") : t("backup.create")}</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            {t("backup.description")}
                        </DialogContentText>

                        <Stack direction="column" sx={{mt: 1}}>
                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("0")} onChange={() => toggleMode("0")}/>}
                                label={t("backup.mode.complete")}/>

                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("1")} onChange={() => toggleMode("1")}/>}
                                label={t("backup.mode.worlds")} disabled={modes.includes("0")}/>

                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("2")} onChange={() => toggleMode("2")}/>}
                                label={t("backup.mode.plugins")} disabled={modes.includes("0")}/>

                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("3")} onChange={() => toggleMode("3")}/>}
                                label={t("backup.mode.config")} disabled={modes.includes("0")}/>

                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("4")} onChange={() => toggleMode("4")}/>}
                                label={t("backup.mode.logs")} disabled={modes.includes("0")}/>
                        </Stack>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                        <Button onClick={actionMode ? () => createAction(modes) : executeAction}
                                disabled={modes.length === 0}>{actionMode ? t("action.save") : t("action.create")}</Button>
                    </DialogActions>
                </Box>
            </Dialog>
        </>
    )
}