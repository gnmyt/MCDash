import CodeMirror from "@uiw/react-codemirror";
import {atomone} from "@uiw/codemirror-theme-atomone";
import {Box, Fab} from "@mui/material";
import {useEffect, useState} from "react";
import {patchRequest, request} from "@/common/utils/RequestUtil.js";
import {Save} from "@mui/icons-material";
import {t} from "i18next";

export const FileEditor = ({directory, currentFile, setSnackbar}) => {

    const [fileContent, setFileContent] = useState("");
    const [fileContentChanged, setFileContentChanged] = useState(false);

    useEffect(() => {
        if (currentFile === null) return setFileContent(null);

        request("filebrowser/file?path=." + directory + currentFile.name)
            .then(async (data) => setFileContent(await data.text()));
    }, [currentFile]);

    useEffect(() => {
        return () => setFileContent(null);
    }, []);

    const saveFile = () => {
        patchRequest("filebrowser/file", {path: "." + directory + currentFile.name, content: fileContent}).then(() => {
            setFileContentChanged(false);
            setSnackbar(t("files.file_saved"));
        });
    }

    const updateContent = (value) => {
        setFileContentChanged(true);
        setFileContent(value);
    }

    return (
        <Box display="flex" flexDirection="column" gap={1} marginTop={2} sx={{maxWidth: "85vw"}}>
            {fileContentChanged && <Fab color="secondary" sx={{position: "fixed", bottom: 20, right: 20}}
                                        onClick={saveFile}><Save/></Fab>}
            <CodeMirror value={fileContent === null ? t("files.loading") : fileContent} onChange={updateContent} theme={atomone}/>
        </Box>
    )
}