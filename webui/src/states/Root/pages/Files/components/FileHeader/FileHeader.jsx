import {Box, Chip, CircularProgress, IconButton, Stack, Typography} from "@mui/material";
import {Close, CreateNewFolder, UploadFile} from "@mui/icons-material";
import NewFolderDialog from "@/states/Root/pages/Files/components/FileHeader/components/NewFolderDialog";
import React, {useState} from "react";
import {uploadRequest} from "@/common/utils/RequestUtil.js";

export const FileHeader = ({currentFile, directory, setDirectory, setCurrentFile, updateFiles, setSnackbar}) => {
    const [dialogOpen, setDialogOpen] = useState(false);

    const [loading, setLoading] = useState(false);

    const upload = () => {
        const input = document.createElement("input");
        input.type = "file";
        input.onchange = () => {
            setLoading(true);
            const file = input.files[0];

            uploadRequest("filebrowser/file?path=." + directory, file).then(() => {
                updateFiles();
                setSnackbar("File uploaded successfully!");
                setLoading(false);
            });
        }
        input.click();
    }

    return (
        <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
            <NewFolderDialog open={dialogOpen} setOpen={setDialogOpen} updateFiles={updateFiles}
                             directory={directory} setSnackbar={setSnackbar}/>
            <Typography variant="h5" fontWeight={500} sx={{display: "flex", alignItems: "center"}}>File Manager
                {currentFile === null && directory.split("/").splice(0, directory.split("/").length - 1).map((dir, index) => (
                    <Chip key={index} label={dir || "/"} color="secondary" style={{marginLeft: 5}}
                          onClick={() => setDirectory(directory.substring(0, directory.indexOf(dir) + dir.length + 1))}/>
                ))}
                {loading && <CircularProgress size={20} sx={{marginLeft: 2}} color="secondary"/>}
            </Typography>

            {currentFile !== null && <IconButton color="secondary" onClick={() => setCurrentFile(null)}><Close /></IconButton>}

            {currentFile === null && <Stack direction="row" spacing={1}>
               <IconButton color="secondary" onClick={upload}><UploadFile/></IconButton>
                    <IconButton color="secondary" onClick={() => setDialogOpen(true)}><CreateNewFolder/></IconButton>
            </Stack>}
        </Box>
    );
}