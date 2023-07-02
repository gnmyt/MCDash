import {Menu, MenuItem} from "@mui/material";
import {Delete, Download} from "@mui/icons-material";
import {deleteRequest, downloadRequest} from "@/common/utils/RequestUtil.js";

export const FileDropdown = ({contextMenu, setContextMenu, directory, setFiles, setSnackbar}) => {

    const handleClose = () => {
        setContextMenu(null);
    }

    const deleteFile = (file) => {
        deleteRequest("filebrowser/file", {path: "." + directory + file.name}).then(() => {
            setFiles(files => files.filter((f) => f.name !== file.name));
            setSnackbar("File successfully deleted");
        });
    }

    const deleteFolder = (file) => {
        deleteRequest("filebrowser/folder", {path: "." + directory + file.name}).then(() => {
            setFiles(files => files.filter((f) => f.name !== file.name));
            setSnackbar("Folder successfully deleted");
        });
    }

    const handleDelete = () => {
        if (!contextMenu.file) return;

        if (contextMenu.file.is_folder)
            deleteFolder(contextMenu.file);
        else deleteFile(contextMenu.file);

        handleClose();
    }

    const downloadFile = () => {
        if (!contextMenu.file || contextMenu?.file?.is_folder) return;

        downloadRequest("filebrowser/file?path=." + directory + contextMenu.file.name);

        handleClose();
    }

    return (
        <Menu open={contextMenu !== null} onClose={handleClose} anchorReference="anchorPosition"
              anchorPosition={contextMenu !== null
                  ? {top: contextMenu.mouseY, left: contextMenu.mouseX} : undefined}>
            {!contextMenu?.file?.is_folder && <MenuItem onClick={downloadFile}>
                <Download/>
                Download
            </MenuItem>}
            <MenuItem onClick={handleDelete}>
                <Delete/>
                Delete
            </MenuItem>
        </Menu>
    );
}