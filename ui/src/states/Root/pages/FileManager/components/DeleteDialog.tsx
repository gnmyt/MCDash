import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle
} from "@/components/ui/alert-dialog"
import {deleteRequest} from "@/lib/RequestUtil.ts";
import {toast} from "@/hooks/use-toast.ts";
import {t} from "i18next";

interface DeleteDialogProps {
    path: string;
    isFolder: boolean;
    isOpen: boolean;
    setOpen: (open: boolean) => void;
    updateFiles: () => void;
}

const DeleteDialog = ({path, isFolder, isOpen, setOpen, updateFiles}: DeleteDialogProps) => {

    const handleDelete = () => {
        deleteRequest(isFolder ? "folder" : "files", {path: path}).then(() => {
            setOpen(false);
            updateFiles();

            toast({description: t("files.file_deleted")});
        });
    }

    return (
        <AlertDialog open={isOpen} onOpenChange={setOpen}>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle>{t("action.sure")}</AlertDialogTitle>
                    <AlertDialogDescription>
                        {t("files.delete_description")}
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel>{t("action.cancel")}</AlertDialogCancel>
                    <AlertDialogAction onClick={() => handleDelete()}>{t("action.continue")}</AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>

    )
}

export default DeleteDialog;