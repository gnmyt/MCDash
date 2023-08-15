import {Box, Button, Chip, IconButton, Stack, Tooltip, Typography} from "@mui/material";
import React, {useContext, useState} from "react";
import {deleteRequest, postRequest} from "@/common/utils/RequestUtil.js";
import {PluginsContext} from "@/states/Root/pages/Plugins/contexts/Plugins";
import {Delete} from "@mui/icons-material";
import ActionConfirmDialog from "@components/ActionConfirmDialog";
import {t} from "i18next";

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
            <Typography variant="body2" color="text.secondary">{t("plugins.by")} {author || "Unknown"}</Typography>
            <Typography variant="body1">{description || t("plugins.no_description")}</Typography>

            <ActionConfirmDialog title={t("plugins.delete.title", {name})} description={t("plugins.delete.text", {name})}
                                 onClick={deletePlugin} successMessage="none"
                                    open={deleteDialogOpen} setOpen={setDeleteDialogOpen} />

            <Stack direction="row" justifyContent="flex-end" sx={{mt: 1, alignItems: "center"}} gap={1}>

                {name !== "MinecraftDashboard" && <Tooltip title={t("plugins.delete.tooltip")}>
                    <IconButton size="small" color="error" onClick={() => setDeleteDialogOpen(true)}>
                        <Delete/>
                    </IconButton></Tooltip>}

                <Button variant="contained" size="small" color={enabled ? "error" : "success"} disabled={name === "MinecraftDashboard"}
                        onClick={togglePlugin}>{t(enabled ? t("plugins.disable") : t("plugins.enable"))}</Button>
            </Stack>
        </Box>
    )
}