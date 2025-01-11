import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList,
    BreadcrumbSeparator
} from "@/components/ui/breadcrumb.tsx";
import {FileUpIcon, Home, SaveIcon, XIcon} from "lucide-react";
import {Fragment, useState} from "react";
import {Button} from "@/components/ui/button.tsx";
import {Progress} from "@/components/ui/progress.tsx";
import {patchRequest, uploadChunks} from "@/lib/RequestUtil.ts";
import CreateFolderDialog from "@/states/Root/pages/FileManager/components/CreateFolderDialog.tsx";
import {toast} from "@/hooks/use-toast.ts";
import {t} from "i18next";

interface FileHeaderProps {
    currentFile: string | null;
    directory: string;
    setDirectory: (directory: string) => void;
    setCurrentFile: (file: string | null) => void;
    updateFiles: () => void;
    fileContent: string | undefined;
}

const FileHeader = ({currentFile, directory, setDirectory, setCurrentFile, updateFiles, fileContent}: FileHeaderProps) => {
    const [uploading, setUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);

    const uploadFile = () => {
        const input = document.createElement("input");
        input.type = "file";
        input.accept = "*/*";
        input.style.display = "none";

        input.onchange = (e: Event) => {
            const target = e.target as HTMLInputElement;
            if (target.files && target.files.length > 0) {
                const file = target.files[0];
                setUploading(true);
                uploadChunks(file, directory, setUploadProgress).finally(() => {
                    setUploading(false);
                    updateFiles();
                    toast({description: t("files.file_uploaded")});
                });
            }
        };

        input.click();
    };

    const getPathArray = () => {
        const strings = directory.split("/").splice(0, directory.split("/").length - 1);
        if (strings.length === 0) return ["/"];
        if (strings[0] === "") strings.splice(0, 1);
        if (currentFile) strings.push(currentFile);
        return strings;
    }

    const saveFile = () => {
        patchRequest("files/content", {path: directory + currentFile, content: fileContent}).then(() => {
            toast({description: t("files.file_saved")});
        });
    }

    return (
        <>
            <div className="flex items-center justify-between">
                <Breadcrumb>
                    <BreadcrumbList>
                        <Home className="h-4 w-4 text-blue-500 cursor-pointer" onClick={() => !currentFile && setDirectory("/")}/>
                        <BreadcrumbSeparator className="hidden md:block"/>

                        {getPathArray().map((dir, index) => (
                            <Fragment key={index}>
                                {dir !== currentFile && <>
                                    <BreadcrumbItem key={index} className="cursor-pointer">
                                        <BreadcrumbLink
                                            onClick={() => !currentFile && setDirectory(directory.split("/").splice(0, index + 2).join("/") + "/")}>{dir}</BreadcrumbLink>
                                    </BreadcrumbItem>
                                    <BreadcrumbSeparator className="hidden md:block"/>
                                </>}
                                {dir === currentFile && <BreadcrumbItem key={index}>
                                    <BreadcrumbLink>{dir}</BreadcrumbLink>
                                </BreadcrumbItem>}
                            </Fragment>
                        ))}
                    </BreadcrumbList>
                </Breadcrumb>
                {currentFile === null && <div className="flex space-x-2">
                    {!uploading && <Button variant="outline" className="rounded-full" onClick={uploadFile}>
                        <FileUpIcon className="mr-2 h-4 w-4"/> {t("files.upload_file")}
                    </Button>}
                    <CreateFolderDialog updateFiles={updateFiles} path={directory}/>
                </div>}
                {currentFile !== null && <div className="flex space-x-2">
                    <Button variant="outline" className="rounded-full" onClick={saveFile}>
                        <SaveIcon className="mr-2 h-4 w-4"/> {t("files.save_file")}
                    </Button>
                    <Button variant="destructive" className="rounded-full" onClick={() => setCurrentFile(null)}>
                        <XIcon className="mr-2 h-4 w-4"/> {t("action.exit")}
                    </Button>
                </div>}
            </div>
            {uploading && <Progress value={uploadProgress}/>}
        </>
    )
}

export default FileHeader;