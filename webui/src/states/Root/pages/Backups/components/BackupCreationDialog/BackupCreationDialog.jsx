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

export const BackupCreationDialog = ({open, setOpen, setLoading}) => {

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
        setOpen(false);
        setLoading(true);
        request("backups/", "PUT", {mode: modes.join("")}, {}, false).then(() => {
            setFinished(true);
            setLoading(false);
            updateBackups();
        });
    }

    useEffect(() => {
        if (!open) setModes([]);
    }, [open]);

    return (
        <>
            <Snackbar open={finished} autoHideDuration={5000} onClose={() => setFinished(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setFinished(false)} severity="success" sx={{width: '100%'}}>
                    Backup successfully created
                </Alert>
            </Snackbar>

            <Dialog open={open} onClose={() => setOpen(false)}>
                <Box component="form" noValidate>
                    <DialogTitle>Create a backup</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            What do you want to backup?
                        </DialogContentText>

                        <Stack direction="column" sx={{mt: 1}}>
                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("0")} onChange={() => toggleMode("0")}/>}
                                label="The complete server"/>

                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("1")} onChange={() => toggleMode("1")}/>}
                                label="All loaded worlds" disabled={modes.includes("0")}/>

                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("2")} onChange={() => toggleMode("2")}/>}
                                label="Plug-Ins & their configs" disabled={modes.includes("0")}/>

                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("3")} onChange={() => toggleMode("3")}/>}
                                label="Configuration files" disabled={modes.includes("0")}/>

                            <FormControlLabel
                                control={<Checkbox checked={modes.includes("4")} onChange={() => toggleMode("4")}/>}
                                label="Console logs" disabled={modes.includes("0")}/>
                        </Stack>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(false)}>Cancel</Button>
                        <Button onClick={executeAction}>Create</Button>
                    </DialogActions>
                </Box>
            </Dialog>
        </>
    )
}