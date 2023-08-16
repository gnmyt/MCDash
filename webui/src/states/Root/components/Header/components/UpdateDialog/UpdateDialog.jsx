import {Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle, Snackbar, Typography} from "@mui/material";
import {postRequest} from "@/common/utils/RequestUtil.js";
import {useState} from "react";
import {t} from "i18next";

export const UpdateDialog = ({open, setOpen, latest, current, setVersionInfo}) => {

    const [snackbarOpen, setSnackbarOpen] = useState(false);

    const reload = (reload = false) => {
        setOpen(false);
        setSnackbarOpen(true);
        postRequest("update", {reloadAfterUpdate: reload}).then(() => {
            if (reload) setTimeout(() => window.location.reload(), 5000);
            setTimeout(() => setSnackbarOpen(false), 5000);
            setVersionInfo({available: false, latest, current});
        });
    }

    return (
        <>
            <Snackbar open={snackbarOpen} anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}>
                <Alert severity="info" sx={{width: '100%'}}>
                    {t("update.updating", {latest})}
                </Alert>
            </Snackbar>
            <Dialog open={open} onClose={() => setOpen(false)}>
                <DialogTitle>{t("update.available")} <Typography color="seagreen"
                                                          variant="caption">{current} ➤ {latest}</Typography></DialogTitle>
                <DialogContent>
                    <Typography>
                        {t("update.info")}
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => reload(false)}>{t("update.just_update")}</Button>
                    <Button onClick={() => reload(true)}>{t("update.reload_after")}</Button>
                </DialogActions>
            </Dialog>
        </>
    );
}