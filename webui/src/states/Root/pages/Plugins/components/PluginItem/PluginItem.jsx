import {Box, Button, Chip, IconButton, Stack, Typography} from "@mui/material";
import React, {useContext, useState} from "react";
import {deleteRequest, postRequest} from "@/common/utils/RequestUtil.js";
import {PluginsContext} from "@/states/Root/pages/Plugins/contexts/Plugins";
import {Delete} from "@mui/icons-material";
import ActionConfirmDialog from "@components/ActionConfirmDialog/index.js";

export const PluginItem = ({name, version, author, description, enabled, path}) => {
    const {plugins, updatePlugins} = useContext(PluginsContext);

    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

    const togglePlugin = () => {
        const plugin = plugins.find((p) => p.name === name);

        (!plugin?.enabled ? postRequest("plugin/", {name: name}) : deleteRequest("plugin/", {name: name}))
            .then(() => updatePlugins());

        return true;
    }

    const deletePlugin = () => {
        if (enabled) togglePlugin();

        deleteRequest("filebrowser/file", {path: `plugins/${path}`})
            .then(() => updatePlugins());

        return true;
    }

    return (
        <Box backgroundColor="background.darker" borderRadius={2} padding={2} sx={{mr: 1, mt: 1, width: {xs: "100%", lg: 300}}}>
            <Typography variant="h6" fontWeight={500}>{name} <Chip label={version} size="small" color="secondary" /></Typography>
            <Typography variant="body2" color="text.secondary">by {author || "Unknown"}</Typography>
            <Typography variant="body1">{description || "No description provided"}</Typography>

            <ActionConfirmDialog title={`Delete ${name}`} description={`Are you sure you want to delete ${name}? You need to restart your server to apply the changes after deleting.`}
                                 onClick={deletePlugin} successMessage="none"
                                    open={deleteDialogOpen} setOpen={setDeleteDialogOpen} />

            <Stack direction="row" justifyContent="flex-end" sx={{mt: 1, alignItems: "center"}} gap={1}>

                {name !== "MinecraftDashboard" && <IconButton size="small" color="error" onClick={() => setDeleteDialogOpen(true)}>
                    <Delete />
                </IconButton>}

                <Button variant="contained" size="small" color={enabled ? "error" : "success"} disabled={name === "MinecraftDashboard"}
                        onClick={togglePlugin}>{enabled ? "Disable" : "Enable"}</Button>
            </Stack>
        </Box>
    )
}