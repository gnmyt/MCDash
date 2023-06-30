import {Autocomplete, FormControl, InputLabel, MenuItem, Select, Stack, TextField} from "@mui/material";

const versions = [
    "1.20.1", "1.20", "1.19.4", "1.19.3", "1.19.2", "1.19.1",
    "1.19", "1.18.1", "1.18", "1.17.1", "1.17", "1.16.5",
    "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.16", "1.15.2",
    "1.15.1", "1.15", "1.14.4", "1.14.3", "1.14.2", "1.14.1",
    "1.14", "1.13.2", "1.13.1", "1.13", "1.12.2", "1.12.1",
    "1.12", "1.11.2", "1.11.1", "1.11", "1.10.2", "1.10.1",
    "1.10", "1.9.4", "1.9.3", "1.9.2", "1.9.1", "1.9",
    "1.8.9", "1.8.8",
]

export const Server = ({software, setSoftware, version, setVersion, instanceId,
                           setInstanceId, serverName, setServerName}) => {
    return (
        <>
            <Stack direction="column" justifyContent="space-between" spacing={2}>
                <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2}>
                    <FormControl fullWidth>
                        <InputLabel id="software">Software</InputLabel>
                        <Select labelId="software" label="Software" value={software}
                                onChange={(e) => setSoftware(e.target.value)}>
                            <MenuItem value="spigot">Spigot</MenuItem>
                            <MenuItem value="paper">Paper</MenuItem>
                            <MenuItem value="purpur">Purpur</MenuItem>
                        </Select>
                    </FormControl>

                    <Autocomplete options={versions} fullWidth value={version} onChange={(e, v) => setVersion(v)}
                                  renderInput={(params) => <TextField {...params} label="Version"/>}/>
                </Stack>

                <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2}>
                    <TextField fullWidth label="Server Name" variant="outlined" value={serverName}
                               onChange={(e) => setServerName(e.target.value)}/>
                    <TextField fullWidth label="Instance ID" variant="outlined" value={instanceId}
                               onChange={(e) => setInstanceId(e.target.value)}/>
                </Stack>
            </Stack>
        </>);
}