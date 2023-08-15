import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField} from "@mui/material";
import {useEffect, useState} from "react";
import {putRequest} from "@/common/utils/RequestUtil.js";
import {t} from "i18next";

export const NewFolderDialog = ({open, setOpen, directory, updateFiles, setSnackbar}) => {

    const [folderName, setFolderName] = useState("");

    useEffect(() => {
        setFolderName("");
    }, [open]);

    const create = (event) => {
        if (event) event.preventDefault();
        if (!folderName) return;
        setOpen(false);

        putRequest("filebrowser/folder", {path: "." + directory + folderName}).then(() => {
            updateFiles();
            setSnackbar(t("files.folder_created"));
        });
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)} aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description" component="form" onSubmit={create}>
            <DialogTitle id="alert-dialog-title">
                {t("files.create_folder.title")}
            </DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    {t("files.create_folder.text")}
                </DialogContentText>

                <TextField autoFocus label={t("files.folder")} fullWidth variant="standard" value={folderName}
                           onChange={(e) => setFolderName(e.target.value)}/>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                <Button onClick={create} autoFocus>{t("action.create")}</Button>
            </DialogActions>
        </Dialog>
    );
}