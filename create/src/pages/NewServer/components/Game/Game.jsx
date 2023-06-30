import {
    Checkbox,
    FormControlLabel,
    Link,
    Stack,
    TextField
} from "@mui/material";

const EULA_URL = "https://www.minecraft.net/eula";

export const Game = ({eula, setEula, mcPort, setMcPort, panelPort, setPanelPort}) => {
return (
        <>
            <Stack direction="column" justifyContent="space-between" spacing={2}>
                <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2}>
                    <TextField fullWidth label="Minecraft Port" variant="outlined" value={mcPort}
                               onChange={(e) => setMcPort(e.target.value)} type="number"/>
                    <TextField fullWidth label="Panel Port" variant="outlined" value={panelPort}
                               onChange={(e) => setPanelPort(e.target.value)} type="number"/>
                </Stack>

                <FormControlLabel control={<Checkbox checked={eula} onChange={(e) => setEula(e.target.checked)}/>}
                                  label={<>I accept the <Link href={EULA_URL} underline="hover" target="_blank">EULA</Link></>} />
            </Stack>
        </>
    )
}