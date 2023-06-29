import {Alert, Stack} from "@mui/material";

export const ExistingServer = () => {
    return (
        <>
            <Stack direction="column" justifyContent="space-between" spacing={2}>
                <Alert severity="warning">MCDash only works on servers based on Spigot</Alert>

                <Alert severity="error">Still WIP! Please come back later.</Alert>
            </Stack>
        </>
    )
}