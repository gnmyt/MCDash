import {useEffect} from "react";
import {request} from "@/lib/RequestUtil.ts";
import Editor from "@monaco-editor/react";
import {useTheme} from "@/components/theme-provider.tsx";
import {getLanguageFromFilename} from "@/lib/FileUtil.ts";
import {Skeleton} from "@/components/ui/skeleton.tsx";

interface FileEditorProps {
    directory: string;
    currentFile: string;
    fileContent: string | undefined;
    setFileContent: (fileContent: string | undefined) => void;
}

const FileEditor = ({directory, currentFile, fileContent, setFileContent}: FileEditorProps) => {
    const {theme} = useTheme();

    useEffect(() => {
        if (currentFile === null) return setFileContent(undefined);

        request("files/download?path=" + directory + currentFile)
            .then(async (data) => setFileContent(await data.text()));
    }, [currentFile]);

    useEffect(() => {
        return () => setFileContent(undefined);
    }, []);

    const getEditorTheme = () => {
        if (theme === "system") {
            return window.matchMedia("(prefers-color-scheme: dark)").matches ? "vs-dark" : "light";
        }
        return theme === "dark" ? "vs-dark" : "light";
    };

    return (
        <div className="flex-1 rounded-xl overflow-hidden border bg-card">
            <Editor
                height="100%"
                language={getLanguageFromFilename(currentFile)}
                value={fileContent}
                theme={getEditorTheme()}
                onChange={(value) => setFileContent(value)}
                loading={<Skeleton className="h-full w-full" />}
                options={{
                    minimap: {enabled: false},
                    fontSize: 14,
                    lineNumbers: "on",
                    scrollBeyondLastLine: false,
                    automaticLayout: true,
                    tabSize: 2,
                    wordWrap: "on",
                    padding: {top: 16, bottom: 16},
                }}
            />
        </div>
    )
}

export default FileEditor;