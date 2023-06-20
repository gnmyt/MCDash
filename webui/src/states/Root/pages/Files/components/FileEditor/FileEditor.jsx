import CodeMirror from "@uiw/react-codemirror";
import {atomone} from "@uiw/codemirror-theme-atomone";
import {Box} from "@mui/material";
import {useEffect} from "react";
import {request} from "@/common/utils/RequestUtil.js";

export const FileEditor = ({directory, currentFile, setContentChanged, fileContent, setFileContent}) => {

    useEffect(() => {
        if (currentFile === null) return setFileContent("");

        request("filebrowser/file?path=." + directory + currentFile.name)
            .then(async (data) => setFileContent(await data.text()));
    }, [currentFile]);

    useEffect(() => {
        return () => setFileContent("");
    }, []);

    const updateContent = (value) => {
        setContentChanged(true);
        setFileContent(value);
    }

    return (
        <Box display="flex" flexDirection="column" gap={1} marginTop={2}><CodeMirror
            value={fileContent || "Loading..."}
            onChange={updateContent}
            theme={atomone}
        /></Box>
    )
}