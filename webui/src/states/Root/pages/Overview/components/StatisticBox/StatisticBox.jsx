import {Box, Stack, Typography} from "@mui/material";

export const StatisticBox = ({title, value, icon, color}) => {
    const iconStyle = {borderRadius: 1.5, width: 50, height: 50,
        justifyContent: "center", alignItems: "center", "& svg": {width: 30, height: 30},
        "& img": {width: 50, height: 50, borderRadius: 1.5}};

    return (
        <Box sx={{flex: 1, display: "flex", justifyContent: "center", flexDirection: "column"}}
             backgroundColor="background.darker" borderRadius={1.5}
             padding={2}>
            <Stack direction="row" alignItems="center" gap={2}>
                <Stack sx={{...iconStyle, backgroundColor: color + ".main", color: color + ".contrastText"}}>
                    {icon}
                </Stack>
                <Stack direction="column" justifyContent="center">
                    <Typography variant="h7" fontWeight={600}>{title}</Typography>
                    <Typography variant="body2" fontWeight={500}>{value}</Typography>
                </Stack>
            </Stack>
        </Box>
    )
}