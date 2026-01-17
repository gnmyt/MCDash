import { Line, LineChart, CartesianGrid, XAxis, YAxis } from "recharts";
import { ChartContainer, ChartTooltip, ChartTooltipContent, type ChartConfig } from "@/components/ui/chart";
import { Widget } from "@/types/widget";
import { t } from "i18next";

interface LineChartWidgetProps {
    widget: Widget;
}

const LineChartWidget = ({ widget }: LineChartWidgetProps) => {
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
            <LineChart
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
                    content={<ChartTooltipContent indicator="dot" />}
                />
                <Line
                    dataKey="value"
                    type="monotone"
                    stroke={widget.color}
                    strokeWidth={2}
                    dot={false}
                    activeDot={{ r: 4 }}
                />
            </LineChart>
        </ChartContainer>
    );
};

export default LineChartWidget;
