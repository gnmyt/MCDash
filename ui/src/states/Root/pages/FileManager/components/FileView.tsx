import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table.tsx";
import {FileIcon, Folder, MoreVertical} from "lucide-react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {Button} from "@/components/ui/button.tsx";
import {File} from "@/states/Root/pages/FileManager/FileManager.tsx";
import {convertSize} from "@/lib/FileUtil.ts";
import RenameDialog from "@/states/Root/pages/FileManager/components/RenameDialog.tsx";
import {useState} from "react";
import {downloadRequest} from "@/lib/RequestUtil.ts";
import DeleteDialog from "@/states/Root/pages/FileManager/components/DeleteDialog.tsx";
import {t} from "i18next";

interface FileViewProps {
    files: File[];
    click: (file: File) => void;
    directory: string;
    updateFiles: () => void;
}

const FileView = ({files, click, directory, updateFiles}: FileViewProps) => {
    const [isDeleteOpen, setIsDeleteOpen] = useState(false);
    const [isRenameOpen, setIsRenameOpen] = useState(false);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);

    const handleRename = (file: File) => {
        setSelectedFile(file);
        setIsRenameOpen(true);
    }

    const handleDownload = (file: File) => {
        downloadRequest("files/download?path=" + directory + file.name);
    }

    const handleDelete = (file: File) => {
        setSelectedFile(file);
        setIsDeleteOpen(true);
    }

    return (
        <div className="rounded-md border flex-grow overflow-hidden">
            <RenameDialog directory={directory} updateFiles={updateFiles}
                          selectedFile={selectedFile} isOpen={isRenameOpen} setOpen={setIsRenameOpen} />
            <DeleteDialog path={directory + selectedFile?.name} isOpen={isDeleteOpen} setOpen={setIsDeleteOpen}
                          updateFiles={updateFiles} isFolder={selectedFile?.is_folder ?? false} />
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead className="w-[50%]">{t("files.table.name")}</TableHead>
                        <TableHead className="w-[12%]">{t("files.table.last_modified")}</TableHead>
                        <TableHead className="w-[5%]">{t("files.table.size")}</TableHead>
                        <TableHead className="w-[4%] text-right">{t("files.table.actions")}</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {
                        files.map((item) => (
                            <TableRow key={item.name} onClick={() => click(item)} className="cursor-pointer hover:accent">
                                <TableCell className="font-medium">
                                    <div className="flex items-center">
                                        {item.is_folder ? (
                                            <Folder className="mr-2 h-4 w-4 text-blue-500"/>
                                        ) : (
                                            <FileIcon className="mr-2 h-4 w-4 text-gray-500"/>
                                        )}
                                        {item.name}
                                    </div>
                                </TableCell>
                                <TableCell>{new Date(item.last_modified).toLocaleString()}</TableCell>
                                <TableCell>{!item.is_folder && convertSize(parseInt(item.size))}</TableCell>
                                <TableCell className="text-right">
                                    <DropdownMenu>
                                        <DropdownMenuTrigger asChild>
                                            <Button
                                                variant="ghost"
                                                className="h-8 w-8 p-0 rounded-full"
                                                onClick={(event) => event.stopPropagation()}>
                                                <span className="sr-only">Open</span>
                                                <MoreVertical className="h-4 w-4"/>
                                            </Button>
                                        </DropdownMenuTrigger>
                                        <DropdownMenuContent
                                            align="end"
                                            className="w-[160px]"
                                            onClick={(event) => event.stopPropagation()}>
                                            <DropdownMenuItem onClick={() => handleRename(item)}>{t("files.rename")}</DropdownMenuItem>
                                            {!item.is_folder && <DropdownMenuItem onClick={() => handleDownload(item)}>{t("files.download")}</DropdownMenuItem>}
                                            <DropdownMenuItem onClick={() => handleDelete(item)} className="text-red-600">{t("files.delete")}</DropdownMenuItem>
                                        </DropdownMenuContent>
                                    </DropdownMenu>
                                </TableCell>
                            </TableRow>

                        ))}
                </TableBody>
            </Table>
        </div>
    )
}

export default FileView;