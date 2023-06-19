import {Menu, MenuItem} from "@mui/material";
import {Delete} from "@mui/icons-material";
import {deleteRequest} from "@/common/utils/RequestUtil.js";

export const FileDropdown = ({contextMenu, setContextMenu, directory, setFiles}) => {

    const handleClose = () => {
        setContextMenu(null);
    }

    const deleteFile = (file) => {
        deleteRequest("filebrowser/file", {path: "." + directory + file.name})
            .then(() => setFiles(files => files.filter((f) => f.name !== file.name)));
    }

    const deleteFolder = (file) => {
        deleteRequest("filebrowser/folder", {path: "." + directory + file.name})
            .then(() => setFiles(files => files.filter((f) => f.name !== file.name)));
    }

    const handleDelete = () => {
        if (!contextMenu.file) return;

        if (contextMenu.file.is_folder)
            deleteFolder(contextMenu.file);
        else deleteFile(contextMenu.file);

        handleClose();
    }

    return (
        <Menu open={contextMenu !== null} onClose={handleClose} anchorReference="anchorPosition"
              anchorPosition={contextMenu !== null
                  ? {top: contextMenu.mouseY, left: contextMenu.mouseX} : undefined}>
            <MenuItem onClick={handleDelete} disableRipple>
                <Delete/>
                Delete
            </MenuItem>
        </Menu>
    );
}