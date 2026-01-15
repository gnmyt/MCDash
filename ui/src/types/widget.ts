export type WidgetType = 
    | 'LINE_CHART' 
    | 'AREA_CHART' 
    | 'BAR_CHART' 
    | 'STAT_CARD' 
    | 'PROGRESS' 
    | 'INFO_CARD';

export interface WidgetDataPoint {
    timestamp: number;
    label: string;
    value: number;
}

export interface WidgetSize {
    width: number;
    height: number;
    minWidth: number;
    minHeight: number;
}

export interface WidgetMetadata {
    [key: string]: string | number | boolean;
}

export interface Widget {
    id: string;
    translationKey: string;
    type: WidgetType;
    color: string;
    unit?: string;
    defaultSize: WidgetSize;
    data?: WidgetDataPoint[];
    metadata?: WidgetMetadata;
}

export interface WidgetLayout {
    i: string;
    x: number;
    y: number;
    w: number;
    h: number;
    minW?: number;
    minH?: number;
}

export interface SavedLayout {
    layouts: WidgetLayout[];
    version: number;
}
