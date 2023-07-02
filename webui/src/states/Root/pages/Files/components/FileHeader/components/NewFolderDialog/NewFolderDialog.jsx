import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField} from "@mui/material";
import {useEffect, useState} from "react";
import {putRequest} from "@/common/utils/RequestUtil.js";

export const NewFolderDialog = ({open, setOpen, directory, updateFiles, setSnackbar}) => {

    const [folderName, setFolderName] = useState("");

    useEffect(() => {
        setFolderName("");
    }, [open]);

    const create = (event) => {
        if (event) event.preventDefault();
        setOpen(false);

        putRequest("filebrowser/folder", {path: "." + directory + folderName}).then(() => {
            updateFiles();
            setSnackbar("Folder created successfully");
        });
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)} aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description" component="form" onSubmit={create}>
            <DialogTitle id="alert-dialog-title">
                Create new folder
            </DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    Please enter the name of the new folder.
                </DialogContentText>

                <TextField autoFocus label="Folder" fullWidth variant="standard" value={folderName}
                           onChange={(e) => setFolderName(e.target.value)}/>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>Cancel</Button>
                <Button onClick={create} autoFocus>Create</Button>
            </DialogActions>
        </Dialog>
    );
}