import {Box, Typography} from "@mui/material";

export const StatisticBox = ({title, value}) => {
    return (
        <Box sx={{flex: 1, textAlign: "center", display: "flex", justifyContent: "center", flexDirection: "column"}} backgroundColor="background.darker" borderRadius={1}
             padding={2}>
            <Typography variant="h7" fontWeight={500}>{title}</Typography>
            <Typography variant="h5" fontWeight={500} color="text.primary">{value}</Typography>
        </Box>
    )
}