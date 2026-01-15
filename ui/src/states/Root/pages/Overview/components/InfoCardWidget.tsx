import { Widget } from "@/types/widget";
import { t } from "i18next";
import { 
    CubeIcon, 
    TagIcon, 
    PlugIcon, 
    ListNumbersIcon, 
    GhostIcon, 
    StackIcon, 
    CoffeeIcon, 
    DesktopIcon, 
    CpuIcon,
    type Icon
} from "@phosphor-icons/react";

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

    const fieldIcons: Record<string, Icon> = {
        software: CubeIcon,
        version: TagIcon,
        port: PlugIcon,
        count: ListNumbersIcon,
        entities: GhostIcon,
        loadedChunks: StackIcon,
        javaVersion: CoffeeIcon,
        os: DesktopIcon,
        processors: CpuIcon,
    };

    const entries = Object.entries(metadata);

    return (
        <div className="flex flex-col flex-1 gap-1 overflow-auto">
            {entries.map(([key, value]) => {
                const FieldIcon = fieldIcons[key] || TagIcon;
                return (
                    <div 
                        key={key} 
                        className="flex items-center justify-between py-1.5 px-2 rounded-lg hover:bg-muted/40 transition-colors"
                    >
                        <div className="flex items-center gap-2">
                            <FieldIcon 
                                className="h-3.5 w-3.5 text-muted-foreground" 
                                weight="duotone"
                            />
                            <span className="text-xs text-muted-foreground">
                                {fieldLabels[key] ? t(fieldLabels[key]) : key}
                            </span>
                        </div>
                        <span 
                            className="text-xs font-medium truncate ml-2" 
                            style={{ color: widget.color }}
                        >
                            {typeof value === 'number' ? value.toLocaleString() : String(value)}
                        </span>
                    </div>
                );
            })}
        </div>
    );
};

export default InfoCardWidget;
