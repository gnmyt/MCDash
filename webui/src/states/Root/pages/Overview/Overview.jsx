import {Button, Stack, Typography} from "@mui/material";
import {PowerSettingsNew, Replay} from "@mui/icons-material";

export const Overview = () => {
    return (
        <>
            <Typography variant="h5" fontWeight={500} sx={{mt: 2}}>Quick Control</Typography>

            <Stack spacing={2} direction="row" sx={{mt: 3}}>
                <Button variant="contained" color="error" startIcon={<PowerSettingsNew />}>Shutdown</Button>

                <Button variant="contained" color="warning" startIcon={<Replay />}>Reload</Button>
            </Stack>
        </>
    )
}