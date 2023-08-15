import {
    Alert,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Snackbar
} from "@mui/material";
import {useState} from "react";
import {t} from "i18next";

export const ActionConfirmDialog = ({open, setOpen, title, description, buttonText, onClick = () => {},
                                        successMessage}) => {
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [actionFailed, setActionFailed] = useState(false);

    const confirm = async () => {
        setOpen(false);

        if (!(await onClick())) setActionFailed(true);

        setSnackbarOpen(true);
    }

    return (
        <>
            {successMessage !== "none" &&
                <Snackbar open={snackbarOpen} autoHideDuration={3000} onClose={() => setSnackbarOpen(false)}
                          anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                    <Alert onClose={() => setSnackbarOpen(false)} severity={actionFailed ? "error" : "success"}
                           sx={{width: '100%'}}>
                        {actionFailed ? t("action.failed") : (successMessage || t("action.success"))}
                    </Alert>
                </Snackbar>}
            <Dialog open={open} onClose={() => setOpen(false)} aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">
                    {title || t("action.confirm")}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        {description || t("action.sure")}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                    <Button onClick={confirm} autoFocus>
                        {buttonText || t("action.continue")}
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
}