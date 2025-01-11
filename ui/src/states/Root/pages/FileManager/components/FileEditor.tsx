import {ChangeEvent, useEffect} from "react";
import {Textarea} from "@/components/ui/textarea.tsx";
import {request} from "@/lib/RequestUtil.ts";

interface FileEditorProps {
    directory: string;
    currentFile: string;
    fileContent: string | undefined;
    setFileContent: (fileContent: string | undefined) => void;
}

const FileEditor = ({directory, currentFile, fileContent, setFileContent}: FileEditorProps) => {

    useEffect(() => {
        if (currentFile === null) return setFileContent(undefined);

        request("files/download?path=" + directory + currentFile)
            .then(async (data) => setFileContent(await data.text()));
    }, [currentFile]);

    useEffect(() => {
        return () => setFileContent(undefined);
    }, []);

    const updateContent = (event: ChangeEvent<HTMLTextAreaElement>) => {
        setFileContent(event.target.value);
    }

    return (
        <>
            <Textarea value={fileContent!} onChange={updateContent} className="h-full w-full" />
        </>
    )
}

export default FileEditor;