import { Area, AreaChart, CartesianGrid, XAxis, YAxis } from "recharts";
import { ChartContainer, ChartTooltip, ChartTooltipContent, type ChartConfig } from "@/components/ui/chart";
import { Widget } from "@/types/widget";
import { t } from "i18next";

interface AreaChartWidgetProps {
    widget: Widget;
}

const AreaChartWidget = ({ widget }: AreaChartWidgetProps) => {
    const chartConfig = {
        value: {
            label: t(widget.translationKey),
            color: widget.color,
        },
    } satisfies ChartConfig;

    const data = widget.data?.map(point => ({
        label: point.label,
        value: point.value,
    })) || [];

    return (
        <ChartContainer config={chartConfig} className="h-full w-full">
            <AreaChart
                accessibilityLayer
                data={data}
                margin={{ top: 10, right: 10, left: 0, bottom: 0 }}
            >
                <CartesianGrid vertical={false} strokeDasharray="3 3" />
                <XAxis
                    dataKey="label"
                    tickLine={false}
                    axisLine={false}
                    tickMargin={8}
                    fontSize={10}
                    interval="preserveStartEnd"
                />
                <YAxis
                    tickLine={false}
                    axisLine={false}
                    tickMargin={8}
                    fontSize={10}
                    width={40}
                />
                <ChartTooltip
                    cursor={false}
                    content={<ChartTooltipContent indicator="line" />}
                />
                <defs>
                    <linearGradient id={`fill-${widget.id}`} x1="0" y1="0" x2="0" y2="1">
                        <stop
                            offset="5%"
                            stopColor={widget.color}
                            stopOpacity={0.8}
                        />
                        <stop
                            offset="95%"
                            stopColor={widget.color}
                            stopOpacity={0.1}
                        />
                    </linearGradient>
                </defs>
                <Area
                    dataKey="value"
                    type="monotone"
                    fill={`url(#fill-${widget.id})`}
                    stroke={widget.color}
                    strokeWidth={2}
                />
            </AreaChart>
        </ChartContainer>
    );
};

export default AreaChartWidget;
