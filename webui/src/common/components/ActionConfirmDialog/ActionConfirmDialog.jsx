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
            <Snackbar open={snackbarOpen} autoHideDuration={3000} onClose={() => setSnackbarOpen(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setSnackbarOpen(false)} severity={actionFailed ? "error" : "success"}
                       sx={{width: '100%'}}>
                    {actionFailed ? "Could not execute action" : (successMessage || "Action executed successfully")}
                </Alert>
            </Snackbar>
            <Dialog open={open} onClose={() => setOpen(false)} aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">
                    {title || "Confirm action"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        {description || "Are you sure you want to confirm this action?"}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>Cancel</Button>
                    <Button onClick={confirm} autoFocus>
                        {buttonText || "Yes, continue"}
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
}