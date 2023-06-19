import {useEffect, useState} from "react";
import {jsonRequest, patchRequest} from "@/common/utils/RequestUtil.js";
import {useLocation, useNavigate} from "react-router-dom";
import FileEditor from "@/states/Root/pages/Files/components/FileEditor";
import FileDropdown from "@/states/Root/pages/Files/components/FileDropdown";
import FileView from "@/states/Root/pages/Files/components/FileView";
import FileHeader from "@/states/Root/pages/Files/components/FileHeader/index.js";

export const Files = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const [contextMenu, setContextMenu] = useState(null);

    const [files, setFiles] = useState([]);
    const [directory, setDirectory] = useState(location.pathname.substring(6));

    const [fileContent, setFileContent] = useState("");
    const [currentFile, setCurrentFile] = useState(null);
    const [fileContentChanged, setFileContentChanged] = useState(false);

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
        if (file.is_folder) changeDirectory(file.name);
        else setCurrentFile(file);
    }

    const saveFile = () => {
        patchRequest("filebrowser/file", {path: "." + directory + currentFile.name, content: fileContent})
            .then(() => setFileContentChanged(false));
    }

    useEffect(() => {
        navigate("/files" + directory);
        jsonRequest("filebrowser/folder?path=." + directory)
            .then((data) => setFiles(data.sort((a, b) => b.is_folder - a.is_folder)))
            .catch(() => changeDirectory(".."));
    }, [directory]);

    useEffect(() => {
        if (location.pathname === "/files") {
            setDirectory("/");
            setCurrentFile(null);
        }
    }, [location.pathname]);

    return (
        <>

            <FileDropdown setFiles={setFiles} setContextMenu={setContextMenu} contextMenu={contextMenu}
                          directory={directory} />

            <FileHeader directory={directory} currentFile={currentFile} fileContentChanged={fileContentChanged}
                        setDirectory={setDirectory} saveFile={saveFile} setCurrentFile={setCurrentFile} />

            {!currentFile && <FileView files={files} changeDirectory={changeDirectory} click={click}
                                        handleContextMenu={handleContextMenu} />}

            {currentFile && <FileEditor currentFile={currentFile} setContentChanged={setFileContentChanged} directory={directory}
                                        fileContent={fileContent} setFileContent={setFileContent} />}
        </>

    )
}