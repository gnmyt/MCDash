import * as React from "react";
import {SpinnerGapIcon} from "@phosphor-icons/react";
import {Backup} from "@/types/backup.ts";
import BackupCard from "@/states/Root/pages/Backups/components/BackupCard.tsx";
import CreateBackupDialog from "@/states/Root/pages/Backups/components/CreateBackupDialog.tsx";
import {deleteRequest, downloadRequest, jsonRequest, postRequest} from "@/lib/RequestUtil.ts";
import {useEffect} from "react";
import {toast} from "@/hooks/use-toast.ts";
import {t} from "i18next";

const Backups = () => {
    const [backups, setBackups] = React.useState<Backup[]>([]);
    const [isCreating, setIsCreating] = React.useState(false);

    const handleCreateBackup = async (backupMode: number) => {
        setIsCreating(true);

        await postRequest("backups/create", {backupMode});

        await fetchBackups();
        setIsCreating(false);

        toast({description: t("backup.created")});
    }

    const handleRestore = async (id: number, haltAfterRestore: boolean) => {
        await postRequest(`backups/restore`, {backupName: id, haltAfterRestore});

        toast({description: t("backup.restored")});
    }

    const handleDelete = (id: number) => deleteRequest(`backups/${id}`).then(async () => {
        await fetchBackups();

        toast({description: t("backup.delete.success")});
    });

    const handleDownload = (id: number) => downloadRequest(`backups/download/${id}`)

    const fetchBackups = async () => jsonRequest("backups/list").then((data) => {
        setBackups(data.backups.sort((a: { id: number }, b: { id: number }) => b.id - a.id));
    });

    useEffect(() => {
        fetchBackups();
    }, []);

    return (
        <div className="p-8">
            <div className="mb-8 flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">{t("backup.title")}</h1>
                    <p className="text-muted-foreground">{t("backup.subtitle")}</p>
                </div>
                <CreateBackupDialog onBackup={handleCreateBackup} disabled={isCreating}/>
            </div>

            {isCreating && (
                <div className="mb-8 flex items-center gap-2 rounded-lg border bg-muted p-4">
                    <SpinnerGapIcon className="h-4 w-4 animate-spin"/>
                    <span>{t("backup.creating")}</span>
                </div>
            )}

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {backups.map((backup) => (
                    <BackupCard key={backup.id} backup={backup} onRestore={handleRestore}
                                onDelete={handleDelete} onDownload={handleDownload}/>
                ))}
                {backups.length === 0 && (
                    <div className="text-muted-foreground text-center col-span-full">
                        {t("backup.none_found")}
                    </div>
                )}
            </div>
        </div>
    );
}

export default Backups;