import * as React from "react";
import { CheckIcon } from "@phosphor-icons/react";

import { Button } from "@/components/ui/button.tsx";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog.tsx";
import { Label } from "@/components/ui/label.tsx";
import { BackupType } from "@/types/backup.ts";
import {t} from "i18next";

interface CreateBackupDialogProps {
    onBackup: (data: number) => Promise<void>;
    disabled?: boolean;
}

export const BACKUP_TYPES: { value: BackupType; bit: number }[] = [
    { value: 'PLUGINS', bit: 2 },
    { value: 'CONFIGS',bit: 4 },
    { value: 'LOGS', bit: 8 },
];

const CreateBackupDialog = ({ onBackup, disabled }: CreateBackupDialogProps) => {
    const [open, setOpen] = React.useState(false);
    const [selectedTypes, setSelectedTypes] = React.useState<BackupType[]>([]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setOpen(false);

        let backupBit = selectedTypes.reduce((acc, type) => acc | BACKUP_TYPES.find(t => t.value === type)!.bit, 0);

        if (selectedTypes.length === BACKUP_TYPES.length) backupBit = 0;

        await onBackup(backupBit);
        setSelectedTypes([]);
    }

    const toggleType = (type: BackupType) => {
        setSelectedTypes(prev => prev.includes(type) ? prev.filter(t => t !== type) : [...prev, type]);
    }

    const selectAll = () => {
        setSelectedTypes(BACKUP_TYPES.map(t => t.value));
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button disabled={disabled}>{t("backup.create")}</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
                <form onSubmit={handleSubmit}>
                    <DialogHeader>
                        <DialogTitle>{t("backup.create")}</DialogTitle>
                        <DialogDescription>
                            {t("backup.description")}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <div className="flex items-center justify-between">
                                <Label>{t("backup.include")}</Label>
                                <Button type="button" variant="ghost" size="sm" onClick={selectAll}>
                                    {t("backup.select_all")}
                                </Button>
                            </div>
                            <div className="grid grid-cols-2 gap-2">
                                {BACKUP_TYPES.map((type) => (
                                    <Button key={type.value} type="button" variant="outline" className="justify-start"
                                        onClick={() => toggleType(type.value)}>
                                        <div className={`mr-2 flex h-4 w-4 items-center justify-center rounded-sm border ${
                                            selectedTypes.includes(type.value) ? "bg-primary border-primary" : "border-primary"}`}>
                                            {selectedTypes.includes(type.value) && (
                                                <CheckIcon className="h-3 w-3 text-primary-foreground" />
                                            )}
                                        </div>
                                        {t("backup.mapping." + type.value.toLowerCase())}
                                    </Button>
                                ))}
                            </div>
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="submit" disabled={selectedTypes.length === 0}>
                            {t("action.create")}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}

export default CreateBackupDialog;