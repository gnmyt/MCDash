import React, {useEffect, useState} from "react";
import {downloadRequest, jsonRequest} from "@/common/utils/RequestUtil.js";
import {useLocation, useNavigate} from "react-router-dom";
import FileEditor from "@/states/Root/pages/Files/components/FileEditor";
import FileDropdown from "@/states/Root/pages/Files/components/FileDropdown";
import FileView from "@/states/Root/pages/Files/components/FileView";
import FileHeader from "@/states/Root/pages/Files/components/FileHeader";
import {Alert, Snackbar} from "@mui/material";

export const Files = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const [contextMenu, setContextMenu] = useState(null);

    const [files, setFiles] = useState([]);
    const [directory, setDirectory] = useState(location.pathname.substring(6));

    const [currentFile, setCurrentFile] = useState(null);

    const [snackbar, setSnackbar] = useState("");

    const handleContextMenu = (event, file) => {
        event.preventDefault();
        setContextMenu(contextMenu === null ? {mouseX: event.clientX + 2, mouseY: event.clientY - 6, file} : null);
    };

    const changeDirectory = (newDirectory) => {
        if (currentFile !== null) setCurrentFile(null);

        if (newDirectory === "..") {
            if (directory === "/") return;
            setDirectory(directory.substring(0, directory.length - 1).split("/").slice(0, -1).join("/") + "/");
        } else {
            setDirectory(directory + newDirectory + "/");
        }
    }

    const click = (file) => {
        if (file.is_folder) return changeDirectory(file.name);
        if (file.size > 1000000) return downloadRequest("filebrowser/file?path=." + directory + file.name, file.name);

        setCurrentFile(file);
    }

    useEffect(() => {
        navigate("/files" + directory);
        updateFiles();
    }, [directory]);

    const updateFiles = () => {
        jsonRequest("filebrowser/folder?path=." + directory)
            .then((data) => setFiles(data.sort((a, b) => b.is_folder - a.is_folder)))
            .catch(() => changeDirectory(".."));
    }

    useEffect(() => {
        if (location.pathname === "/files") {
            setDirectory("/");
            setCurrentFile(null);
        }
    }, [location.pathname]);

    return (
        <>
            <Snackbar open={snackbar !== ""} autoHideDuration={3000} onClose={() => setSnackbar("")}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setSnackbar("")} severity="success" sx={{width: '100%'}}>
                    {snackbar}
                </Alert>
            </Snackbar>

            <FileDropdown setFiles={setFiles} setContextMenu={setContextMenu} contextMenu={contextMenu}
                          directory={directory} setSnackbar={setSnackbar} />

            <FileHeader directory={directory} currentFile={currentFile} setDirectory={setDirectory}
                        setCurrentFile={setCurrentFile} updateFiles={updateFiles} setSnackbar={setSnackbar} />

            {!currentFile && <FileView files={files} changeDirectory={changeDirectory} click={click}
                                        handleContextMenu={handleContextMenu} />}

            {currentFile && <FileEditor currentFile={currentFile} directory={directory}
                                        setSnackbar={setSnackbar} />}
        </>
    );
}