import {Button, IconButton, Link, Stack, TextField, Typography} from "@mui/material";
import {Bolt, CopyAll} from "@mui/icons-material";

const demo_command = "curl -sSL https://create.mcdash.gnmyt.dev/install.sh && echo \"this command won't work. Still WIP\"";

export const Finished = () => {
    const copyCommand = () => navigator.clipboard.writeText(demo_command);

    return (
        <>
            <Stack spacing={2} sx={{mt: 3}}>
                <TextField multiline fullWidth label="Run this command on your server" value={demo_command} InputProps={{
                    readOnly: true, endAdornment: <IconButton onClick={copyCommand}><CopyAll/></IconButton>}}/>

                <Typography variant="body2" color="text.secondary" justifyContent="center" textAlign="center">
                    OR
                </Typography>

                <Stack spacing={1} justifyContent="center" textAlign="center">
                    <Button variant="contained">Run directly on SSH Server</Button>
                    <Stack direction="row" alignItems="center" spacing={0.5} justifyContent="center">
                        <Bolt color="warning" fontSize={"small"}/>
                        <Typography variant="body2" color="text.secondary">This uses the</Typography>
                        <Link href="https://tools.gnmyt.dev/linux/ssh" target="_blank" rel="noopener noreferrer"
                              color="#ffa726" underline="hover" variant="body2">PowerTools API</Link>
                    </Stack>
                </Stack>
            </Stack>
        </>
    )
}