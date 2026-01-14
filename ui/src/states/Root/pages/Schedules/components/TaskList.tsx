import { 
    GearIcon,
    PencilSimpleIcon,
    TrashIcon
} from "@phosphor-icons/react";
import { t } from "i18next";
import { ScheduleTask, ScheduleAction } from "@/types/schedule";
import { Button } from "@/components/ui/button";
import { deleteRequest } from "@/lib/RequestUtil";
import { toast } from "@/hooks/use-toast";
import { useState } from "react";
import TaskFormDialog from "./TaskFormDialog";

interface TaskListProps {
    tasks: ScheduleTask[];
    scheduleId: number;
    actions: ScheduleAction[];
    onRefresh: () => Promise<void>;
}

const TaskList = ({ tasks, scheduleId, actions, onRefresh }: TaskListProps) => {
    const [editingTask, setEditingTask] = useState<ScheduleTask | null>(null);

    const handleDeleteTask = async (taskId: number) => {
        try {
            await deleteRequest(`schedules/${scheduleId}/tasks/${taskId}`);
            await onRefresh();
            toast({ description: t("schedules.task_deleted") });
        } catch (error) {
            toast({ description: t("schedules.error.delete_task"), variant: "destructive" });
        }
    };

    const getActionLabel = (actionId: string) => {
        const action = actions.find(a => a.id === actionId);
        return action ? t(action.translationKey) : actionId;
    };

    if (tasks.length === 0) {
        return (
            <div className="text-center py-6 text-muted-foreground">
                <p className="text-sm">{t("schedules.no_tasks")}</p>
            </div>
        );
    }

    return (
        <>
            <div className="space-y-2">
                {tasks.map((task) => (
                    <div 
                        key={task.id}
                        className="flex items-center justify-between p-3 rounded-lg bg-muted/50 hover:bg-muted transition-colors"
                    >
                        <div className="flex items-center gap-3 min-w-0 flex-1">
                            <div className="h-8 w-8 rounded-lg bg-background flex items-center justify-center shrink-0">
                                <GearIcon className="h-4 w-4" />
                            </div>
                            <div className="min-w-0 flex-1">
                                <p className="text-sm font-medium">{getActionLabel(task.actionId)}</p>
                                {task.metadata && (
                                    <p className="text-xs text-muted-foreground truncate">
                                        {task.metadata}
                                    </p>
                                )}
                            </div>
                        </div>
                        <div className="flex items-center gap-1 shrink-0">
                            <Button
                                variant="ghost"
                                size="icon"
                                className="h-8 w-8 rounded-lg"
                                onClick={() => setEditingTask(task)}
                            >
                                <PencilSimpleIcon className="h-4 w-4" />
                            </Button>
                            <Button
                                variant="ghost"
                                size="icon"
                                className="h-8 w-8 rounded-lg text-destructive hover:text-destructive"
                                onClick={() => handleDeleteTask(task.id)}
                            >
                                <TrashIcon className="h-4 w-4" />
                            </Button>
                        </div>
                    </div>
                ))}
            </div>

            {editingTask && (
                <TaskFormDialog
                    open={!!editingTask}
                    onOpenChange={(open: boolean) => !open && setEditingTask(null)}
                    task={editingTask}
                    scheduleId={scheduleId}
                    actions={actions}
                    onSuccess={async () => {
                        setEditingTask(null);
                        await onRefresh();
                    }}
                />
            )}
        </>
    );
};

export default TaskList;
