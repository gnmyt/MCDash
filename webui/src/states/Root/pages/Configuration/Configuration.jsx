import React, {useContext, useEffect, useState} from "react";
import {PropertiesContext} from "@/states/Root/pages/Configuration/contexts/Properties";
import {Box, Button, Stack, Switch, TextField, Typography} from "@mui/material";
import ConfigurationItem from "@/states/Root/pages/Configuration/components/ConfigurationItem";
import {Dns} from "@mui/icons-material";
import {SSHStatusContext} from "@/states/Root/pages/Configuration/contexts/SSHStatus";
import {t} from "i18next";

export const Configuration = () => {
    const {properties, updateProperties} = useContext(PropertiesContext);
    const {sshPort, sshStatus, updateSshPort, updateSshStatus} = useContext(SSHStatusContext);

    const [sshPortState, setSshPortState] = useState(5174);

    useEffect(() => {
        setSshPortState(sshPort);
    }, [sshPort]);

    const retrieveUsername = () => atob(localStorage.getItem("token")).split(":")[0];

    return (
        <div>
            <Typography variant="h5" fontWeight={500}>{t("configuration.services")}</Typography>

            <Stack direction="row" sx={{mt: 2}} flexWrap="wrap">
                <Box backgroundColor="background.darker" borderRadius={2} padding={2}
                     sx={{mr: 1, mt: 1, width: {xs: "100%", lg: 350}}}>
                    <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
                        <Stack direction="row" spacing={1} alignItems="center">
                            <Dns/>
                            <Typography variant="h5" fontWeight={600}>{t("configuration.ssh.server")}</Typography>
                        </Stack>
                        <Switch checked={sshStatus} onChange={updateSshStatus}/>
                    </Stack>
                    <Typography variant="body2">{t("configuration.ssh.description")}</Typography>
                    <Stack direction="row" spacing={1} alignItems="center" justifyContent="center" sx={{mt: 2}}>
                        <TextField fullWidth label={t("configuration.ssh.port")} variant="outlined" value={sshPortState} type="number" size="small"
                                   onChange={(event) => setSshPortState(event.target.value)} onBlur={updateSshPort}/>
                        <Button href={`sftp://${retrieveUsername()}@${window.location.hostname}:${sshPort}/`}
                                variant="contained" disabled={!sshStatus} fullWidth >{t("configuration.ssh.connect")}</Button>
                    </Stack>
                </Box>
            </Stack>

            <Typography variant="h5" fontWeight={500} mt={2}>{t("nav.configuration")}</Typography>

            <Stack direction="row" sx={{mt: 2}} flexWrap="wrap">
                {properties.map((property) =>
                    <ConfigurationItem property={property} updateProperties={updateProperties} key={property.name}/>
                )}
            </Stack>

        </div>
    )
}