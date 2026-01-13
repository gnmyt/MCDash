import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table.tsx";
import {FileIcon, FolderIcon, DotsThreeVerticalIcon} from "@phosphor-icons/react";
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
        <div className="rounded-xl border flex-grow overflow-hidden bg-card">
            <RenameDialog directory={directory} updateFiles={updateFiles}
                          selectedFile={selectedFile} isOpen={isRenameOpen} setOpen={setIsRenameOpen} />
            <DeleteDialog path={directory + selectedFile?.name} isOpen={isDeleteOpen} setOpen={setIsDeleteOpen}
                          updateFiles={updateFiles} isFolder={selectedFile?.is_folder ?? false} />
            <Table className="text-base">
                <TableHeader>
                    <TableRow className="hover:bg-transparent">
                        <TableHead className="w-[50%] h-14 text-base font-semibold">{t("files.table.name")}</TableHead>
                        <TableHead className="w-[12%] h-14 text-base font-semibold">{t("files.table.last_modified")}</TableHead>
                        <TableHead className="w-[5%] h-14 text-base font-semibold">{t("files.table.size")}</TableHead>
                        <TableHead className="w-[4%] h-14 text-base font-semibold text-right">{t("files.table.actions")}</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {
                        files.map((item) => (
                            <TableRow key={item.name} onClick={() => click(item)} className="cursor-pointer h-14 transition-colors">
                                <TableCell className="font-medium py-4">
                                    <div className="flex items-center gap-3">
                                        {item.is_folder ? (
                                            <FolderIcon className="h-5 w-5 text-primary" weight="fill"/>
                                        ) : (
                                            <FileIcon className="h-5 w-5 text-muted-foreground"/>
                                        )}
                                        <span className="text-base">{item.name}</span>
                                    </div>
                                </TableCell>
                                <TableCell className="py-4 text-muted-foreground">{new Date(item.last_modified).toLocaleString()}</TableCell>
                                <TableCell className="py-4 text-muted-foreground">{!item.is_folder && convertSize(parseInt(item.size))}</TableCell>
                                <TableCell className="text-right py-4">
                                    <DropdownMenu>
                                        <DropdownMenuTrigger asChild>
                                            <Button
                                                variant="ghost"
                                                className="h-10 w-10 p-0 rounded-xl"
                                                onClick={(event) => event.stopPropagation()}>
                                                <span className="sr-only">Open</span>
                                                <DotsThreeVerticalIcon className="h-5 w-5"/>
                                            </Button>
                                        </DropdownMenuTrigger>
                                        <DropdownMenuContent
                                            align="end"
                                            className="w-[180px] rounded-xl p-2"
                                            onClick={(event) => event.stopPropagation()}>
                                            <DropdownMenuItem onClick={() => handleRename(item)} className="rounded-lg h-10 text-base cursor-pointer">{t("files.rename")}</DropdownMenuItem>
                                            {!item.is_folder && <DropdownMenuItem onClick={() => handleDownload(item)} className="rounded-lg h-10 text-base cursor-pointer">{t("files.download")}</DropdownMenuItem>}
                                            <DropdownMenuItem onClick={() => handleDelete(item)} className="rounded-lg h-10 text-base cursor-pointer text-red-600">{t("files.delete")}</DropdownMenuItem>
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