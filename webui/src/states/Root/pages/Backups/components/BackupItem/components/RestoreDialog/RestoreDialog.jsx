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
import {t} from "i18next";

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
                    {t("backup.restored")}
                </Alert>
            </Snackbar>

            <Dialog open={open} onClose={() => setOpen(false)}>
                <Box component="form" noValidate>
                    <DialogTitle>{t("backup.restoring")}</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            {t("backup.restore_text")}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                        <Button onClick={() => executeAction(true)}>{t("backup.restore_and_stop")}</Button>
                        <Button onClick={() => executeAction(false)} color="error">{t("backup.only_restore")}</Button>
                    </DialogActions>
                </Box>
            </Dialog>
        </>
    );
}