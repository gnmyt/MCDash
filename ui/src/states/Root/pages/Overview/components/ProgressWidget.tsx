import { Widget } from "@/types/widget";
import { Progress } from "@/components/ui/progress";
import { t } from "i18next";

interface ProgressWidgetProps {
    widget: Widget;
}

const ProgressWidget = ({ widget }: ProgressWidgetProps) => {
    const metadata = widget.metadata || {};
    const used = (metadata.used as number) ?? 0;
    const max = (metadata.max as number) ?? 100;
    const allocated = metadata.allocated as number | undefined;
    const percentage = (metadata.percentage as number) ?? (max > 0 ? (used / max) * 100 : 0);

    const getProgressColor = () => {
        if (percentage > 90) return 'bg-red-500';
        if (percentage > 75) return 'bg-yellow-500';
        return '';
    };

    return (
        <div className="flex flex-col justify-center h-full px-2 gap-3">
            <div className="flex justify-between items-baseline">
                <span className="text-2xl font-bold" style={{ color: widget.color }}>
                    {used.toLocaleString()} {widget.unit || 'MB'}
                </span>
                <span className="text-sm text-muted-foreground">
                    / {max.toLocaleString()} {widget.unit || 'MB'}
                </span>
            </div>
            <Progress 
                value={percentage} 
                className="h-2"
                indicatorClassName={getProgressColor()}
            />
            <div className="flex justify-between text-xs text-muted-foreground">
                <span>{percentage.toFixed(1)}% {t("overview.widgets.used")}</span>
                {allocated !== undefined && (
                    <span>{t("overview.widgets.allocated")}: {allocated.toLocaleString()} MB</span>
                )}
            </div>
        </div>
    );
};

export default ProgressWidget;
