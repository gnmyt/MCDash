import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Widget } from "@/types/widget";
import { t } from "i18next";
import AreaChartWidget from "./AreaChartWidget";
import LineChartWidget from "./LineChartWidget";
import BarChartWidget from "./BarChartWidget";
import StatCardWidget from "./StatCardWidget";
import ProgressWidget from "./ProgressWidget";
import InfoCardWidget from "./InfoCardWidget";
import { DotsSixVerticalIcon } from "@phosphor-icons/react";

interface WidgetCardProps {
    widget: Widget;
}

const WidgetCard = ({ widget }: WidgetCardProps) => {
    const renderWidgetContent = () => {
        switch (widget.type) {
            case 'AREA_CHART':
                return <AreaChartWidget widget={widget} />;
            case 'LINE_CHART':
                return <LineChartWidget widget={widget} />;
            case 'BAR_CHART':
                return <BarChartWidget widget={widget} />;
            case 'STAT_CARD':
                return <StatCardWidget widget={widget} />;
            case 'PROGRESS':
                return <ProgressWidget widget={widget} />;
            case 'INFO_CARD':
                return <InfoCardWidget widget={widget} />;
            default:
                return <div className="text-muted-foreground text-center">Unknown widget type</div>;
        }
    };

    const isChartWidget = ['AREA_CHART', 'LINE_CHART', 'BAR_CHART'].includes(widget.type);

    return (
        <Card className="h-full flex flex-col overflow-hidden">
            <CardHeader className="py-3 px-4 flex-shrink-0">
                <div className="flex items-center justify-between">
                    <CardTitle className="text-sm font-medium">
                        {t(widget.translationKey)}
                    </CardTitle>
                    <DotsSixVerticalIcon 
                        className="h-4 w-4 text-muted-foreground cursor-grab drag-handle" 
                        weight="bold"
                    />
                </div>
            </CardHeader>
            <CardContent className={`flex-1 min-h-0 ${isChartWidget ? 'pb-2 px-2' : 'pb-4 px-4'}`}>
                {renderWidgetContent()}
            </CardContent>
        </Card>
    );
};

export default WidgetCard;
