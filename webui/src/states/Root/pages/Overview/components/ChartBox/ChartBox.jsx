import {Box, Stack, Typography} from "@mui/material";
import {LineChart} from "@mui/x-charts";

export const ChartBox = ({title, value, formatter = (v) => v, icon, color}) => {
    const iconStyle = {borderRadius: 0.5, width: 25, height: 25,
        justifyContent: "center", alignItems: "center", "& svg": {width: 22, height: 22}};

    return (
        <Box sx={{flex: 1, display: "flex", justifyContent: "center", flexDirection: "column"}}
             backgroundColor="background.darker" borderRadius={1.5} padding={2}>
            <Stack direction="row" justifyContent="space-between" alignItems="center">
                <Stack direction="row" alignItems="center" gap={1}>
                    <Stack sx={{...iconStyle, backgroundColor: color + ".main", color: color + ".contrastText"}}>
                        {icon}
                    </Stack>
                    <Typography variant="h7" fontWeight={500}>{title}</Typography>
                </Stack>
                <Typography variant="h7" fontWeight={500}>{formatter(value[value.length - 1])}</Typography>
            </Stack>
            {value.length >= 1 && <LineChart
                series={[{type: 'line', data: value, valueFormatter: formatter}]}
                xAxis={[{
                    data: value.map((_, i) => i), valueFormatter: (n) => new Date(Date.now()
                        - (value.length - n) * 1000).toLocaleTimeString()
                }]}
                margin={{top: 10, bottom: 0, left: 0, right: 0}}
                height={40}
                sx={{'& .MuiMarkElement-root': {display: 'none'}, '& .MuiChartsAxis-root': {display: 'none'}}}
            />}
        </Box>
    )
}