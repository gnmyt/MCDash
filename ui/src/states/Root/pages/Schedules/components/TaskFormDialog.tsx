import * as React from "react";
import { t } from "i18next";
import { ScheduleAction, ScheduleTask } from "@/types/schedule";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { postRequest, putRequest } from "@/lib/RequestUtil";
import { toast } from "@/hooks/use-toast";

interface TaskFormDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    scheduleId: number;
    actions: ScheduleAction[];
    onSuccess: () => Promise<void>;
    task?: ScheduleTask;
}

const TaskFormDialog = ({ open, onOpenChange, scheduleId, actions, onSuccess, task }: TaskFormDialogProps) => {
    const isEdit = !!task;
    
    const [selectedActionId, setSelectedActionId] = React.useState<string>(task?.actionId ?? "");
    const [metadata, setMetadata] = React.useState(task?.metadata ?? "");
    const [isSubmitting, setIsSubmitting] = React.useState(false);

    const selectedAction = actions.find(a => a.id === selectedActionId);

    React.useEffect(() => {
        if (task) {
            setSelectedActionId(task.actionId);
            setMetadata(task.metadata || "");
        } else if (actions.length > 0 && !selectedActionId) {
            setSelectedActionId(actions[0].id);
            setMetadata("");
        }
    }, [task, actions, open]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        
        try {
            const data = {
                actionId: selectedActionId,
                metadata: selectedAction?.inputType !== "NONE" ? metadata : "",
                ...(isEdit && task ? { executionOrder: task.executionOrder } : {})
            };
            
            if (isEdit && task) {
                await putRequest(`schedules/${scheduleId}/tasks/${task.id}`, data);
                toast({ description: t("schedules.task_updated") });
            } else {
                await postRequest(`schedules/${scheduleId}/tasks`, data);
                toast({ description: t("schedules.task_created") });
            }
            
            await onSuccess();
            onOpenChange(false);
            
            if (!isEdit && actions.length > 0) {
                setSelectedActionId(actions[0].id);
                setMetadata("");
            }
        } catch {
            toast({ 
                description: t(isEdit ? "schedules.error.update_task" : "schedules.error.create_task"), 
                variant: "destructive" 
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleActionChange = (value: string) => {
        setSelectedActionId(value);
        const newAction = actions.find(a => a.id === value);
        if (newAction?.inputType === "NONE") {
            setMetadata("");
        }
    };

    const needsInput = selectedAction && selectedAction.inputType !== "NONE";
    const isTextarea = selectedAction?.inputType === "TEXTAREA";
    const isNumber = selectedAction?.inputType === "NUMBER";

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="sm:max-w-[425px] rounded-xl">
                <form onSubmit={handleSubmit}>
                    <DialogHeader>
                        <DialogTitle className="text-lg">
                            {isEdit ? t("schedules.edit_task") : t("schedules.create_task")}
                        </DialogTitle>
                        <DialogDescription>
                            {isEdit ? t("schedules.edit_task_description") : t("schedules.create_task_description")}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-6">
                        <div className="grid gap-2">
                            <Label>{t("schedules.task.type")}</Label>
                            <Select value={selectedActionId} onValueChange={handleActionChange}>
                                <SelectTrigger className="rounded-xl">
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    {actions.map((action) => (
                                        <SelectItem key={action.id} value={action.id}>
                                            {t(action.translationKey)}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        
                        {needsInput && selectedAction.inputTranslationKey && (
                            <div className="grid gap-2">
                                <Label>{t(selectedAction.inputTranslationKey)}</Label>
                                {isTextarea ? (
                                    <Textarea
                                        value={metadata}
                                        onChange={(e) => setMetadata(e.target.value)}
                                        placeholder={t(selectedAction.inputTranslationKey)}
                                        className="rounded-xl resize-none"
                                        rows={3}
                                    />
                                ) : (
                                    <Input
                                        type={isNumber ? "number" : "text"}
                                        value={metadata}
                                        onChange={(e) => setMetadata(e.target.value)}
                                        placeholder={t(selectedAction.inputTranslationKey)}
                                        className="rounded-xl"
                                    />
                                )}
                            </div>
                        )}
                    </div>
                    <DialogFooter>
                        <Button 
                            type="submit" 
                            disabled={isSubmitting || !selectedActionId} 
                            size="lg" 
                            className="w-full h-12 rounded-xl text-base"
                        >
                            {isEdit ? t("action.save") : t("action.create")}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
};

export default TaskFormDialog;
