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
    ClockIcon,
    SkullIcon,
    SwordIcon,
    GameControllerIcon,
    ShieldCheckIcon,
    EyeIcon,
    WarningCircleIcon,
    CheckCircleIcon,
    XCircleIcon,
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
        time: "overview.widgets.info.time",
        difficulty: "overview.widgets.info.difficulty",
        hardcore: "overview.widgets.info.hardcore",
        pvp: "overview.widgets.info.pvp",
        gamemode: "overview.widgets.info.gamemode",
        spawnProtection: "overview.widgets.info.spawn_protection",
        viewDistance: "overview.widgets.info.view_distance",
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
        time: ClockIcon,
        difficulty: WarningCircleIcon,
        hardcore: SkullIcon,
        pvp: SwordIcon,
        gamemode: GameControllerIcon,
        spawnProtection: ShieldCheckIcon,
        viewDistance: EyeIcon,
    };

    const formatValue = (key: string, value: unknown): string => {
        if (typeof value === 'boolean') {
            return value ? t('action.yes', 'Yes') : t('action.no', 'No');
        }
        if (value === null || value === undefined) {
            return '-';
        }
        return String(value);
    };

    const getBooleanIcon = (value: unknown): Icon | null => {
        if (typeof value === 'boolean') {
            return value ? CheckCircleIcon : XCircleIcon;
        }
        return null;
    };

    const entries = Object.entries(metadata);

    return (
        <div className="flex flex-col flex-1 gap-1 overflow-auto">
            {entries.map(([key, value]) => {
                const FieldIcon = fieldIcons[key] || TagIcon;
                const BoolIcon = getBooleanIcon(value);
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
                        <div className="flex items-center gap-1.5">
                            {BoolIcon && (
                                <BoolIcon 
                                    className={`h-3.5 w-3.5 ${value ? 'text-green-500' : 'text-red-500'}`}
                                    weight="fill"
                                />
                            )}
                            <span 
                                className="text-xs font-medium truncate" 
                                style={{ color: widget.color }}
                            >
                                {formatValue(key, value)}
                            </span>
                        </div>
                    </div>
                );
            })}
        </div>
    );
};

export default InfoCardWidget;
