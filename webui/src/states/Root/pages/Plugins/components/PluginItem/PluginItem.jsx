import {Box, Button, Chip, Stack, Typography} from "@mui/material";
import React, {useContext} from "react";
import {deleteRequest, postRequest} from "@/common/utils/RequestUtil.js";
import {PluginsContext} from "@/states/Root/pages/Plugins/contexts/Plugins/index.js";

export const PluginItem = ({name, version, author, description, enabled}) => {
    const {plugins, updatePlugins} = useContext(PluginsContext);

    const togglePlugin = (pluginName) => {
        const plugin = plugins.find((p) => p.name === pluginName);

        (!plugin?.enabled ? postRequest("plugin/", {name: pluginName}) : deleteRequest("plugin/", {name: pluginName}))
            .then(() => updatePlugins());

        return true;
    }

    return (
        <Box backgroundColor="background.darker" borderRadius={2} padding={2} width={320} sx={{mr: 1, mt: 1}}>
            <Typography variant="h6" fontWeight={500}>{name} <Chip label={version} size="small" color="secondary" /></Typography>
            <Typography variant="body2" color="text.secondary">by {author || "Unknown"}</Typography>
            <Typography variant="body1">{description || "No description provided"}</Typography>

            <Stack direction="row" justifyContent="flex-end" sx={{mt: 1}}>
                <Button variant="contained" size="small" color={enabled ? "error" : "success"} disabled={name === "MinecraftDashboard"}
                        onClick={() => togglePlugin(name)}>{enabled ? "Disable" : "Enable"}</Button>
            </Stack>
        </Box>
    )
}