import {useCallback, useEffect, useState} from "react";
import FileView from "@/states/Root/pages/FileManager/components/FileView.tsx";
import {downloadRequest, jsonRequest} from "@/lib/RequestUtil.ts";
import { useLocation, useNavigate } from "react-router-dom";
import FileHeader from "@/states/Root/pages/FileManager/components/FileHeader.tsx";
import FileEditor from "@/states/Root/pages/FileManager/components/FileEditor.tsx";

export interface File {
    name: string;
    size: string;
    last_modified: number;
    is_folder: boolean;
}

const FileManager = () => {
    const [files, setFiles] = useState([]);
    const location = useLocation();
    const [directory, setDirectory] = useState(location.pathname.substring(6));
    const [currentFile, setCurrentFile] = useState<string | null>(null);
    const [fileContent, setFileContent] = useState<string | undefined>("");

    const navigate = useNavigate();

    const changeDirectory = useCallback((newDirectory: string) => {
        if (currentFile !== null) setCurrentFile(null);

        if (newDirectory === "..") {
            if (directory === "/") return;
            setDirectory(directory.substring(0, directory.length - 1).split("/").slice(0, -1).join("/") + "/");
        } else {
            setDirectory(directory + newDirectory + "/");
        }
    }, [currentFile, directory]);

    const onClick = (file: File) => {
        if (file.is_folder) return changeDirectory(file.name);
        if (parseInt(file.size) > 1000000) return downloadRequest("files/download?path=" + directory + file.name);

        setCurrentFile(file.name);
    };

    const updateFiles = () => {
        jsonRequest("files/list?path=." + directory)
            .then((data) => setFiles(data.files.sort((a: File, b: File) => (b.is_folder ? 1 : 0) - (a.is_folder ? 1 : 0))))
            .catch(() => changeDirectory(".."));
    }

    useEffect(() => {
        navigate("/files" + directory);
        updateFiles();
    }, [directory, location.pathname]);

    return (
        <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
            <FileHeader currentFile={currentFile} directory={directory} setDirectory={setDirectory} setCurrentFile={setCurrentFile}
                        updateFiles={updateFiles} fileContent={fileContent} />
            {!currentFile && <FileView files={files} click={onClick} directory={directory} updateFiles={updateFiles} />}
            {currentFile && <FileEditor currentFile={currentFile} directory={directory} fileContent={fileContent} setFileContent={setFileContent} />}
        </div>
    );
};

export default FileManager;
