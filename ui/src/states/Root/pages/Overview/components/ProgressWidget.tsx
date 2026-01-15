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
        <div className="flex flex-col gap-2">
            <div className="flex items-baseline justify-between">
                <div className="flex items-baseline gap-1.5">
                    <span className="text-2xl font-bold tracking-tight" style={{ color: widget.color }}>
                        {used.toLocaleString()}
                    </span>
                    <span className="text-sm text-muted-foreground">
                        / {max.toLocaleString()} {widget.unit || 'MB'}
                    </span>
                </div>
                <span 
                    className="text-sm font-semibold"
                    style={{ color: widget.color }}
                >
                    {Math.round(percentage)}%
                </span>
            </div>
            <Progress 
                value={percentage} 
                className="h-2"
                indicatorClassName={getProgressColor() || undefined}
                style={!getProgressColor() ? { '--progress-color': widget.color } as React.CSSProperties : undefined}
            />
            {allocated !== undefined && (
                <span className="text-xs text-muted-foreground">
                    {t("overview.widgets.allocated")}: {allocated.toLocaleString()} MB
                </span>
            )}
        </div>
    );
};

export default ProgressWidget;
