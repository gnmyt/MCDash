import {
    Alert,
    Box, Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle, Snackbar,
} from "@mui/material";
import React, {useState} from "react";
import {postRequest} from "@/common/utils/RequestUtil.js";

export const RestoreDialog = ({open, setOpen, id}) => {

    const [actionFinished, setActionFinished] = useState(false);

    const executeAction = (haltAfterRestore = false) => {
        setOpen(false);
        postRequest("backups/restore", {backup_id: id, halt: haltAfterRestore})
            .then(() => setActionFinished(true))
            .catch(() => setActionFinished(true));
    }

    return (
        <>
            <Snackbar open={actionFinished} autoHideDuration={5000} onClose={() => setActionFinished(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setActionFinished(false)} severity="success" sx={{width: '100%'}}>
                    Backup successfully restored
                </Alert>
            </Snackbar>

            <Dialog open={open} onClose={() => setOpen(false)}>
                <Box component="form" noValidate>
                    <DialogTitle>Restoring backup</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            MCDash will restore this backup while runtime. Since Minecraft automatically saves the
                            worlds every few seconds from memory, your world might get corrupted. To prevent this, you
                            can stop the server without saving the world from memory. Any unsaved changes made to the
                            server while runtime will be lost. This does not apply if you want to restore logs or other
                            files that are not getting automatically saved by Minecraft.
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(false)}>Cancel</Button>
                        <Button onClick={() => executeAction(true)}>Restore & Stop</Button>
                        <Button onClick={() => executeAction(false)} color="error">Only Restore</Button>
                    </DialogActions>
                </Box>
            </Dialog>
        </>
    );
}