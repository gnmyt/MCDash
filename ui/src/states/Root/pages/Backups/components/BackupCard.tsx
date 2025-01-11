import {Archive, Download, Settings, FileText, RotateCcw, Trash2} from "lucide-react";
import {Button} from "@/components/ui/button.tsx";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog.tsx";
import {Backup, BackupType} from "@/types/backup.ts";
import {ReactNode} from "react";
import {convertSize} from "@/lib/FileUtil.ts";
import {t} from "i18next";

const TYPE_ICONS: Record<BackupType, ReactNode> = {
    ROOT: <Archive className="h-4 w-4"/>,
    PLUGINS: <Settings className="h-4 w-4"/>,
    CONFIGS: <Settings className="h-4 w-4"/>,
    LOGS: <FileText className="h-4 w-4"/>,
}

interface BackupCardProps {
    backup: Backup;
    onRestore: (id: number, haltAfterRestore: boolean) => void;
    onDelete: (id: number) => void;
    onDownload: (id: number) => void;
}

const BackupCard = ({backup, onRestore, onDelete, onDownload}: BackupCardProps) => {
    return (
        <Card>
            <CardHeader>
                <CardTitle className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <Archive className="h-5 w-5 text-muted-foreground"/>
                        <span>{t("backup.name")}</span>
                    </div>
                    <span className="text-sm font-normal text-muted-foreground">
                        {convertSize(backup.size)}
                    </span>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className="flex flex-col gap-4">
                    <div className="text-sm text-muted-foreground">
                        {t("backup.time")} {new Date(backup.id).toLocaleString()}
                    </div>
                    <div className="flex gap-2">
                        {backup.modes.map((type) => (
                            <div className="flex items-center gap-1 rounded-md bg-muted px-2 py-1 text-sm" key={type}>
                                {TYPE_ICONS[type]}
                                <span>{t(`backup.mapping.${type.toLowerCase()}`)}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </CardContent>
            <CardFooter className="flex flex-wrap justify-end gap-2 px-6 pb-6">
                <AlertDialog>
                    <AlertDialogTrigger asChild>
                        <Button variant="outline" size="sm" className="flex-1 sm:flex-initial">
                            <RotateCcw className="mr-2 h-4 w-4"/>
                            {t("backup.restore")}
                        </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                        <AlertDialogHeader>
                            <AlertDialogTitle>{t("backup.restoring")}</AlertDialogTitle>
                            <AlertDialogDescription>
                                {t("backup.restore_text")}
                            </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                            <AlertDialogCancel>{t("action.cancel")}</AlertDialogCancel>
                            <AlertDialogCancel onClick={() => onRestore(backup.id, false)}>
                                {t("backup.only_restore")}
                            </AlertDialogCancel>
                            <AlertDialogAction onClick={() => onRestore(backup.id, true)}>
                                {t("backup.restore_and_stop")}
                            </AlertDialogAction>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialog>
                <Button variant="outline" size="sm" onClick={() => onDownload(backup.id)}
                        className="flex-1 sm:flex-initial">
                    <Download className="mr-2 h-4 w-4"/>
                    {t("backup.download")}
                </Button>
                <AlertDialog>
                    <AlertDialogTrigger asChild>
                        <Button variant="destructive" size="sm" className="flex-1 sm:flex-initial">
                            <Trash2 className="mr-2 h-4 w-4"/>
                            {t("backup.delete.button")}
                        </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                        <AlertDialogHeader>
                            <AlertDialogTitle>{t("backup.delete.title")}</AlertDialogTitle>
                            <AlertDialogDescription>
                                {t("backup.delete.text")}
                            </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                            <AlertDialogCancel>
                                {t("action.cancel")}
                            </AlertDialogCancel>
                            <AlertDialogAction onClick={() => onDelete(backup.id)}>
                                {t("backup.delete.yes")}
                            </AlertDialogAction>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialog>
            </CardFooter>
        </Card>
    );
}

export default BackupCard;