import {useState, useEffect, useCallback} from "react";
import {useNavigate} from "react-router-dom";
import {t} from "i18next";
import {
    FileTextIcon,
    FloppyDiskIcon,
    CaretRightIcon,
    CaretDownIcon,
    ToggleLeftIcon,
    HashIcon,
    TextAaIcon,
    ListBulletsIcon,
    BracketsCurlyIcon,
    ProhibitIcon,
    GearIcon,
    FileIcon,
    SpinnerIcon,
    ArrowSquareOutIcon,
} from "@phosphor-icons/react";
import {jsonRequest, postRequest} from "@/lib/RequestUtil";
import {ConfigFile, ConfigValue} from "@/types/resource";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Switch} from "@/components/ui/switch";
import {ScrollArea} from "@/components/ui/scroll-area";
import {Skeleton} from "@/components/ui/skeleton";
import {Badge} from "@/components/ui/badge";
import {Tooltip, TooltipContent, TooltipTrigger} from "@/components/ui/tooltip";
import {toast} from "@/hooks/use-toast";
import {cn} from "@/lib/utils";

interface ConfigEditorProps {
    resourceType: string;
    resourceFileName: string;
    resourceName: string;
}

const formatFileName = (name: string): string => {
    return name
        .replace(/\.(yml|yaml|json)$/i, "")
        .replace(/[-_]/g, " ")
        .replace(/\b\w/g, (c) => c.toUpperCase());
};

const formatKeyName = (key: string): string => {
    if (/^\[\d+\]$/.test(key)) return key;

    return key
        .replace(/([a-z])([A-Z])/g, "$1 $2")
        .replace(/[-_]/g, " ")
        .replace(/\b\w/g, (c) => c.toUpperCase())
        .replace(/\s+/g, " ")
        .trim();
};

const getFileColor = (name: string): string => {
    if (name.endsWith(".json")) return "text-yellow-500";
    if (name.endsWith(".yml") || name.endsWith(".yaml")) return "text-blue-500";
    return "text-muted-foreground";
};

export const ConfigEditor = ({resourceType, resourceFileName, resourceName}: ConfigEditorProps) => {
    const navigate = useNavigate();
    const [configFiles, setConfigFiles] = useState<ConfigFile[]>([]);
    const [selectedFile, setSelectedFile] = useState<ConfigFile | null>(null);
    const [configContent, setConfigContent] = useState<Record<string, ConfigValue> | null>(null);
    const [originalContent, setOriginalContent] = useState<Record<string, ConfigValue> | null>(null);
    const [loadingFiles, setLoadingFiles] = useState(true);
    const [loadingContent, setLoadingContent] = useState(false);
    const [saving, setSaving] = useState(false);

    const hasChanges = JSON.stringify(configContent) !== JSON.stringify(originalContent);

    const fetchConfigFiles = useCallback(async () => {
        try {
            const data = await jsonRequest(
                `resources/config/list?type=${resourceType}&fileName=${encodeURIComponent(resourceFileName)}`
            );
            setConfigFiles(data.files || []);
            if (data.files?.length > 0) {
                setSelectedFile(data.files[0]);
            }
        } catch (error) {
            console.error("Failed to fetch config files:", error);
        } finally {
            setLoadingFiles(false);
        }
    }, [resourceType, resourceFileName]);

    const fetchConfigContent = useCallback(async () => {
        if (!selectedFile) return;
        setLoadingContent(true);
        try {
            const data = await jsonRequest(
                `resources/config/get?type=${resourceType}&fileName=${encodeURIComponent(resourceFileName)}&configPath=${encodeURIComponent(selectedFile.path)}`
            );
            setConfigContent(data.content || {});
            setOriginalContent(data.content || {});
        } catch (error) {
            console.error("Failed to fetch config content:", error);
            toast({description: t("resources.config.load_failed"), variant: "destructive"});
        } finally {
            setLoadingContent(false);
        }
    }, [selectedFile, resourceType, resourceFileName]);

    useEffect(() => {
        fetchConfigFiles();
    }, [fetchConfigFiles]);

    useEffect(() => {
        if (selectedFile) {
            fetchConfigContent();
        }
    }, [selectedFile, fetchConfigContent]);

    const handleSave = async () => {
        if (!selectedFile || !configContent) return;
        setSaving(true);
        try {
            const result = await postRequest("resources/config/save", {
                type: resourceType,
                fileName: resourceFileName,
                configPath: selectedFile.path,
                content: configContent,
            });

            if (result.error) {
                toast({description: result.error, variant: "destructive"});
            } else {
                toast({description: t("resources.config.saved")});
                setOriginalContent(configContent);
            }
        } catch (error) {
            toast({description: t("resources.config.save_failed"), variant: "destructive"});
        } finally {
            setSaving(false);
        }
    };

    const updateValue = (path: string[], value: ConfigValue) => {
        if (!configContent) return;

        const newContent = JSON.parse(JSON.stringify(configContent));
        let current: any = newContent;

        for (let i = 0; i < path.length - 1; i++) {
            current = current[path[i]];
        }
        current[path[path.length - 1]] = value;

        setConfigContent(newContent);
    };

    if (loadingFiles) {
        return (
            <div className="rounded-xl border bg-card">
                <div className="flex items-center gap-2 p-4 border-b">
                    <GearIcon className="h-4 w-4 text-muted-foreground"/>
                    <Skeleton className="h-5 w-32"/>
                </div>
                <div className="p-4 space-y-2">
                    {[...Array(3)].map((_, i) => (
                        <Skeleton key={i} className="h-10 w-full"/>
                    ))}
                </div>
            </div>
        );
    }

    if (configFiles.length === 0) {
        return (
            <div className="rounded-xl border bg-card p-8 text-center">
                <div className="h-12 w-12 rounded-full bg-muted flex items-center justify-center mx-auto mb-4">
                    <FileTextIcon className="h-6 w-6 text-muted-foreground"/>
                </div>
                <h3 className="font-medium mb-1">{t("resources.config.no_files")}</h3>
                <p className="text-sm text-muted-foreground">{t("resources.config.no_files_description")}</p>
            </div>
        );
    }

    return (
        <div className="rounded-xl border bg-card flex flex-col flex-1 min-h-[400px] overflow-hidden">
            <div className="flex items-center justify-between px-4 py-3 border-b shrink-0">
                <div className="flex items-center gap-2">
                    <GearIcon className="h-4 w-4 text-muted-foreground"/>
                    <h3 className="font-medium text-sm">{t("resources.config.title")}</h3>
                    {hasChanges && (
                        <Badge variant="secondary" className="text-xs">
                            {t("resources.config.unsaved")}
                        </Badge>
                    )}
                </div>
                <Button
                    size="sm"
                    onClick={handleSave}
                    disabled={saving || !hasChanges}
                    className={cn(!hasChanges && "opacity-50")}
                >
                    {saving ? (
                        <SpinnerIcon className="h-4 w-4 mr-1.5 animate-spin"/>
                    ) : (
                        <FloppyDiskIcon className="h-4 w-4 mr-1.5"/>
                    )}
                    {t("action.save")}
                </Button>
            </div>

            <div className="flex flex-1 min-h-0">
                <div className="w-56 border-r shrink-0 flex flex-col">
                    <div className="p-2 border-b">
                        <p className="text-xs font-medium text-muted-foreground px-2">
                            {t("resources.config.files")} ({configFiles.length})
                        </p>
                    </div>
                    <ScrollArea className="flex-1">
                        <div className="p-2 space-y-1">
                            {configFiles.map((file) => (
                                <Tooltip key={file.path}>
                                    <TooltipTrigger asChild>
                                        <button
                                            onClick={() => setSelectedFile(file)}
                                            className={cn(
                                                "w-full flex items-center gap-2.5 px-3 py-2.5 rounded-lg text-sm transition-all",
                                                selectedFile?.path === file.path
                                                    ? "bg-primary text-primary-foreground shadow-sm"
                                                    : "hover:bg-muted text-foreground"
                                            )}
                                        >
                                            <FileIcon
                                                className={cn(
                                                    "h-4 w-4 shrink-0",
                                                    selectedFile?.path === file.path
                                                        ? "text-primary-foreground"
                                                        : getFileColor(file.name)
                                                )}
                                            />
                                            <span className="truncate font-medium">
                                                {formatFileName(file.name)}
                                            </span>
                                        </button>
                                    </TooltipTrigger>
                                    <TooltipContent side="right">
                                        <p className="font-mono text-xs">{file.path}</p>
                                    </TooltipContent>
                                </Tooltip>
                            ))}
                        </div>
                    </ScrollArea>
                </div>

                <div className="flex-1 min-w-0 flex flex-col">
                    {selectedFile && (
                        <div className="flex items-center justify-between px-4 py-2 border-b bg-muted/30 shrink-0">
                            <p className="text-xs text-muted-foreground font-mono truncate">
                                {selectedFile.path}
                            </p>
                            <Tooltip>
                                <TooltipTrigger asChild>
                                    <button
                                        onClick={() => {
                                            const basePath = resourceType === "plugin" ? "plugins" : "datapacks";
                                            navigate(`/files/${basePath}/${resourceName}/${selectedFile.path}`);
                                        }}
                                        className="p-1 rounded hover:bg-muted transition-colors"
                                    >
                                        <ArrowSquareOutIcon className="h-4 w-4 text-muted-foreground"/>
                                    </button>
                                </TooltipTrigger>
                                <TooltipContent>
                                    <p className="text-xs">{t("resources.config.open_in_files")}</p>
                                </TooltipContent>
                            </Tooltip>
                        </div>
                    )}
                    <ScrollArea className="flex-1">
                        <div className="p-4">
                            {loadingContent ? (
                                <div className="space-y-4">
                                    {[...Array(6)].map((_, i) => (
                                        <div key={i} className="flex items-center gap-3">
                                            <Skeleton className="h-4 w-4 rounded"/>
                                            <Skeleton className="h-4 w-28"/>
                                            <Skeleton className="h-9 flex-1"/>
                                        </div>
                                    ))}
                                </div>
                            ) : configContent && Object.keys(configContent).length > 0 ? (
                                <div className="space-y-1">
                                    {Object.entries(configContent).map(([key, val]) => (
                                        <ConfigValueEditor
                                            key={key}
                                            value={val}
                                            path={[key]}
                                            onChange={updateValue}
                                            keyName={key}
                                            isRoot
                                        />
                                    ))}
                                </div>
                            ) : configContent ? (
                                <div className="flex flex-col items-center justify-center py-12 text-center">
                                    <div
                                        className="h-10 w-10 rounded-full bg-muted flex items-center justify-center mb-3">
                                        <FileTextIcon className="h-5 w-5 text-muted-foreground"/>
                                    </div>
                                    <p className="text-sm text-muted-foreground">
                                        {t("resources.config.empty_file")}
                                    </p>
                                </div>
                            ) : (
                                <div className="flex flex-col items-center justify-center py-12 text-center">
                                    <p className="text-sm text-muted-foreground">
                                        {t("resources.config.select_file")}
                                    </p>
                                </div>
                            )}
                        </div>
                    </ScrollArea>
                </div>
            </div>
        </div>
    );
};

interface ConfigValueEditorProps {
    value: ConfigValue;
    path: string[];
    onChange: (path: string[], value: ConfigValue) => void;
    keyName?: string;
    isRoot?: boolean;
}

const getValueIcon = (value: ConfigValue) => {
    if (value === null) return <ProhibitIcon className="h-3.5 w-3.5 text-muted-foreground"/>;
    if (typeof value === "boolean") return <ToggleLeftIcon className="h-3.5 w-3.5 text-purple-500"/>;
    if (typeof value === "number") return <HashIcon className="h-3.5 w-3.5 text-blue-500"/>;
    if (typeof value === "string") return <TextAaIcon className="h-3.5 w-3.5 text-green-500"/>;
    if (Array.isArray(value)) return <ListBulletsIcon className="h-3.5 w-3.5 text-orange-500"/>;
    if (typeof value === "object") return <BracketsCurlyIcon className="h-3.5 w-3.5 text-cyan-500"/>;
    return null;
};

const ConfigValueEditor = ({value, path, onChange, keyName, isRoot}: ConfigValueEditorProps) => {
    const [collapsed, setCollapsed] = useState(false);
    const formattedKey = keyName ? formatKeyName(keyName) : undefined;

    if (value === null) {
        return (
            <div className="flex items-center gap-3 py-2 px-3 rounded-lg hover:bg-muted/50 transition-colors">
                {getValueIcon(value)}
                {formattedKey && (
                    <Tooltip>
                        <TooltipTrigger asChild>
                            <span className="text-sm font-medium min-w-[140px] truncate cursor-help">
                                {formattedKey}
                            </span>
                        </TooltipTrigger>
                        <TooltipContent>
                            <p className="font-mono text-xs">{keyName}</p>
                        </TooltipContent>
                    </Tooltip>
                )}
                <span className="text-sm text-muted-foreground italic">null</span>
            </div>
        );
    }

    if (typeof value === "boolean") {
        return (
            <div className="flex items-center gap-3 py-2 px-3 rounded-lg hover:bg-muted/50 transition-colors">
                {getValueIcon(value)}
                {formattedKey && (
                    <Tooltip>
                        <TooltipTrigger asChild>
                            <span className="text-sm font-medium min-w-[140px] truncate cursor-help">
                                {formattedKey}
                            </span>
                        </TooltipTrigger>
                        <TooltipContent>
                            <p className="font-mono text-xs">{keyName}</p>
                        </TooltipContent>
                    </Tooltip>
                )}
                <div className="flex items-center gap-2">
                    <Switch
                        checked={value}
                        onCheckedChange={(checked) => onChange(path, checked)}
                    />
                    <span className={cn("text-xs font-medium", value ? "text-green-500" : "text-muted-foreground")}>
                        {value ? "Enabled" : "Disabled"}
                    </span>
                </div>
            </div>
        );
    }

    if (typeof value === "number") {
        return (
            <div className="flex items-center gap-3 py-2 px-3 rounded-lg hover:bg-muted/50 transition-colors">
                {getValueIcon(value)}
                {formattedKey && (
                    <Tooltip>
                        <TooltipTrigger asChild>
                            <span className="text-sm font-medium min-w-[140px] truncate cursor-help">
                                {formattedKey}
                            </span>
                        </TooltipTrigger>
                        <TooltipContent>
                            <p className="font-mono text-xs">{keyName}</p>
                        </TooltipContent>
                    </Tooltip>
                )}
                <Input
                    type="number"
                    value={value}
                    onChange={(e) => onChange(path, parseFloat(e.target.value) || 0)}
                    className="h-9 max-w-[200px] font-mono"
                />
            </div>
        );
    }

    if (typeof value === "string") {
        const isLong = value.length > 50;
        return (
            <div className="flex items-center gap-3 py-2 px-3 rounded-lg hover:bg-muted/50 transition-colors">
                {getValueIcon(value)}
                {formattedKey && (
                    <Tooltip>
                        <TooltipTrigger asChild>
                            <span className="text-sm font-medium min-w-[140px] truncate cursor-help">
                                {formattedKey}
                            </span>
                        </TooltipTrigger>
                        <TooltipContent>
                            <p className="font-mono text-xs">{keyName}</p>
                        </TooltipContent>
                    </Tooltip>
                )}
                <Input
                    type="text"
                    value={value}
                    onChange={(e) => onChange(path, e.target.value)}
                    className={cn("h-9", isLong ? "flex-1" : "max-w-[300px]")}
                />
            </div>
        );
    }

    if (Array.isArray(value)) {
        return (
            <div className={cn("rounded-lg", isRoot && "bg-muted/30")}>
                <button
                    onClick={() => setCollapsed(!collapsed)}
                    className="w-full flex items-center gap-3 py-2.5 px-3 rounded-lg hover:bg-muted/50 transition-colors"
                >
                    {collapsed ? (
                        <CaretRightIcon className="h-4 w-4 text-muted-foreground"/>
                    ) : (
                        <CaretDownIcon className="h-4 w-4 text-muted-foreground"/>
                    )}
                    {getValueIcon(value)}
                    {formattedKey && (
                        <Tooltip>
                            <TooltipTrigger asChild>
                                <span className="text-sm font-medium cursor-help">{formattedKey}</span>
                            </TooltipTrigger>
                            <TooltipContent>
                                <p className="font-mono text-xs">{keyName}</p>
                            </TooltipContent>
                        </Tooltip>
                    )}
                    <Badge variant="secondary" className="text-xs font-normal">
                        {value.length} {value.length === 1 ? "item" : "items"}
                    </Badge>
                </button>
                {!collapsed && (
                    <div className="ml-6 pl-4 border-l-2 border-muted space-y-0.5 pb-2">
                        {value.map((item, index) => (
                            <ConfigValueEditor
                                key={index}
                                value={item}
                                path={[...path, String(index)]}
                                onChange={onChange}
                                keyName={`[${index}]`}
                            />
                        ))}
                        {value.length === 0 && (
                            <p className="text-sm text-muted-foreground italic py-2 px-3">
                                {t("resources.config.empty_array")}
                            </p>
                        )}
                    </div>
                )}
            </div>
        );
    }

    if (typeof value === "object") {
        const entries = Object.entries(value);
        return (
            <div className={cn("rounded-lg", isRoot && "bg-muted/30")}>
                {formattedKey && (
                    <button
                        onClick={() => setCollapsed(!collapsed)}
                        className="w-full flex items-center gap-3 py-2.5 px-3 rounded-lg hover:bg-muted/50 transition-colors"
                    >
                        {collapsed ? (
                            <CaretRightIcon className="h-4 w-4 text-muted-foreground"/>
                        ) : (
                            <CaretDownIcon className="h-4 w-4 text-muted-foreground"/>
                        )}
                        {getValueIcon(value)}
                        <Tooltip>
                            <TooltipTrigger asChild>
                                <span className="text-sm font-medium cursor-help">{formattedKey}</span>
                            </TooltipTrigger>
                            <TooltipContent>
                                <p className="font-mono text-xs">{keyName}</p>
                            </TooltipContent>
                        </Tooltip>
                        <Badge variant="secondary" className="text-xs font-normal">
                            {entries.length} {entries.length === 1 ? "property" : "properties"}
                        </Badge>
                    </button>
                )}
                {!collapsed && (
                    <div className={cn(formattedKey && "ml-6 pl-4 border-l-2 border-muted pb-2", "space-y-0.5")}>
                        {entries.map(([key, val]) => (
                            <ConfigValueEditor
                                key={key}
                                value={val}
                                path={[...path, key]}
                                onChange={onChange}
                                keyName={key}
                            />
                        ))}
                        {entries.length === 0 && (
                            <p className="text-sm text-muted-foreground italic py-2 px-3">
                                {t("resources.config.empty_object")}
                            </p>
                        )}
                    </div>
                )}
            </div>
        );
    }

    return null;
};
