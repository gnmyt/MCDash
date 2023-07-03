import React, {useContext, useEffect, useState} from "react";
import {PropertiesContext} from "@/states/Root/pages/Configuration/contexts/Properties";
import {Box, Button, Stack, Switch, TextField, Typography} from "@mui/material";
import ConfigurationItem from "@/states/Root/pages/Configuration/components/ConfigurationItem";
import {Dns} from "@mui/icons-material";
import {SSHStatusContext} from "@/states/Root/pages/Configuration/contexts/SSHStatus";

export const Configuration = () => {
    const {properties, updateProperties} = useContext(PropertiesContext);
    const {sshPort, sshStatus, updateSshPort, updateSshStatus} = useContext(SSHStatusContext);

    const [sshPortState, setSshPortState] = useState(22);

    useEffect(() => {
        setSshPortState(sshPort);
    }, [sshPort]);

    const retrieveUsername = () => atob(localStorage.getItem("token")).split(":")[0];

    return (
        <div>
            <Typography variant="h5" fontWeight={500}>Services</Typography>

            <Stack direction="row" sx={{mt: 2}} flexWrap="wrap">
                <Box backgroundColor="background.darker" borderRadius={2} padding={2}
                     sx={{mr: 1, mt: 1, width: {xs: "100%", lg: 350}}}>
                    <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
                        <Stack direction="row" spacing={1} alignItems="center">
                            <Dns/>
                            <h2>SSH Server</h2>
                        </Stack>
                        <Switch checked={sshStatus} onChange={updateSshStatus}/>
                    </Stack>
                    <Typography variant="body2">Manage your files and control the console from your computer.</Typography>
                    <Stack direction="row" spacing={1} alignItems="center" justifyContent="center" sx={{mt: 2}}>
                        <TextField fullWidth label="SSH-Port" variant="outlined" value={sshPortState} type="number" size="small"
                                   onChange={(event) => setSshPortState(event.target.value)} onBlur={updateSshPort}/>
                        <Button href={`sftp://${retrieveUsername()}@${window.location.hostname}:${sshPort}/`}
                                variant="contained" disabled={!sshStatus} fullWidth >Connect</Button>
                    </Stack>
                </Box>
            </Stack>

            <Typography variant="h5" fontWeight={500} mt={2}>Configuration</Typography>

            <Stack direction="row" sx={{mt: 2}} flexWrap="wrap">
                {properties.map((property) =>
                    <ConfigurationItem property={property} updateProperties={updateProperties} key={property.name}/>
                )}
            </Stack>

        </div>
    )
}