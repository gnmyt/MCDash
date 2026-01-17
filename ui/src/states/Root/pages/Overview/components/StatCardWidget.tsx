import {Widget} from "@/types/widget";

interface StatCardWidgetProps {
    widget: Widget;
}

const StatCardWidget = ({widget}: StatCardWidgetProps) => {
    const metadata = widget.metadata || {};
    const value = metadata.value ?? 0;
    const max = metadata.max;
    const status = metadata.status as string | undefined;

    const days = metadata.days as number | undefined;
    const hours = metadata.hours as number | undefined;
    const minutes = metadata.minutes as number | undefined;

    const getStatusColor = () => {
        switch (status) {
            case 'good':
                return 'text-green-500';
            case 'warning':
                return 'text-yellow-500';
            case 'critical':
                return 'text-red-500';
            default:
                return '';
        }
    };

    if (days !== undefined && hours !== undefined && minutes !== undefined) {
        return (
            <div className="flex items-baseline gap-1.5">
                <span className="text-3xl font-bold tracking-tight" style={{color: widget.color}}>
                    {days > 0 && `${days}d `}{hours}h {minutes}m
                </span>
            </div>
        );
    }

    return (
        <div className="flex items-baseline gap-1.5">
            <span 
                className={`text-3xl font-bold tracking-tight ${getStatusColor()}`}
                style={{color: !status ? widget.color : undefined}}
            >
                {typeof value === 'number' ? value.toLocaleString() : value}
            </span>
            {max !== undefined && (
                <span className="text-sm text-muted-foreground">
                    / {max} {widget.unit}
                </span>
            )}
            {widget.unit && max === undefined && (
                <span className="text-sm text-muted-foreground">
                    {widget.unit}
                </span>
            )}
        </div>
    );
};

export default StatCardWidget;
