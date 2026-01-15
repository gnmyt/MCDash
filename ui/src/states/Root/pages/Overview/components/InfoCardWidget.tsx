import { Widget } from "@/types/widget";
import { t } from "i18next";

interface InfoCardWidgetProps {
    widget: Widget;
}

const InfoCardWidget = ({ widget }: InfoCardWidgetProps) => {
    const metadata = widget.metadata || {};

    const fieldLabels: Record<string, string> = {
        software: "overview.widgets.info.software",
        version: "overview.widgets.info.version",
        port: "overview.widgets.info.port",
        count: "overview.widgets.info.count",
        entities: "overview.widgets.info.entities",
        loadedChunks: "overview.widgets.info.loaded_chunks",
        javaVersion: "overview.widgets.info.java_version",
        os: "overview.widgets.info.os",
        processors: "overview.widgets.info.processors",
    };

    const entries = Object.entries(metadata);

    return (
        <div className="flex flex-col justify-center px-1 gap-2">
            {entries.map(([key, value]) => (
                <div key={key} className="flex justify-between items-center py-1 border-b border-border/50 last:border-0">
                    <span className="text-sm text-muted-foreground">
                        {fieldLabels[key] ? t(fieldLabels[key]) : key}
                    </span>
                    <span className="text-sm font-medium truncate ml-2" style={{ color: widget.color }}>
                        {typeof value === 'number' ? value.toLocaleString() : String(value)}
                    </span>
                </div>
            ))}
        </div>
    );
};

export default InfoCardWidget;
