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
                            For a seamless world restoration, it is recommended to gracefully stop the server.
                            Failure to do so may result in the server saving the world from memory to disk, potentially
                            causing chunk bugs. If you solely intend to restore configuration changes or logs, you can
                            select the "Only Restore" option.
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