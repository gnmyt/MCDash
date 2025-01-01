import {Button} from "@/components/ui/button.tsx";
import {Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger} from "@/components/ui/dialog.tsx";
import {useState} from "react";
import {FolderPlus} from "lucide-react";
import {Input} from "@/components/ui/input.tsx";
import {putRequest} from "@/lib/RequestUtil.ts";
import {toast} from "@/hooks/use-toast.ts";
import {t} from "i18next";

interface CreateFolderDialogProps {
    path: string;
    updateFiles: () => void;
}

const CreateFolderDialog = ({path, updateFiles}: CreateFolderDialogProps) => {
    const [isCreateFolderOpen, setIsCreateFolderOpen] = useState(false);
    const [newItemName, setNewItemName] = useState("");

    const handleCreateFolder = () => {
        putRequest("folder", {path: path + newItemName}).then(() => {
            setIsCreateFolderOpen(false);
            updateFiles();

            toast({description: t("files.create_folder.feedback")});
        });

        setNewItemName("");
    }

    return (
        <Dialog open={isCreateFolderOpen} onOpenChange={setIsCreateFolderOpen}>
            <DialogTrigger asChild>
                <Button variant="outline" className="rounded-full">
                    <FolderPlus className="mr-2 h-4 w-4" /> {t("files.new_folder")}
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]" aria-label="Create New Folder">
                <DialogHeader>
                    <DialogTitle>{t("files.create_folder.title")}</DialogTitle>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                    <Input
                        value={newItemName}
                        onChange={(e) => setNewItemName(e.target.value)}
                        placeholder={t("files.create_folder.placeholder")}
                        className="col-span-3"
                    />
                </div>
                <Button onClick={handleCreateFolder} className="w-full">{t("files.create_folder.button")}</Button>
            </DialogContent>
        </Dialog>
    )
}

export default CreateFolderDialog;