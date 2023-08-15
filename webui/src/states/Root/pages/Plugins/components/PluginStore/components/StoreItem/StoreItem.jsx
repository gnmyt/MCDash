import {Box, Button, CircularProgress, Link, Stack, Tooltip, Typography} from "@mui/material";
import React, {useContext, useState} from "react";
import {Check, Download, Warning} from "@mui/icons-material";
import {request} from "@/common/utils/RequestUtil.js";
import {PluginsContext} from "@/states/Root/pages/Plugins/contexts/Plugins";
import ResourceIcon from "@/common/assets/images/resource.webp";
import {prettyDownloadCount} from "@/states/Root/pages/Plugins/components/PluginStore/components/StoreItem/utils.js";
import {t} from "i18next";

export const StoreItem = ({id, name, description, icon, downloads, closeStore, installed}) => {
    const {updatePlugins} = useContext(PluginsContext);
    const [installing, setInstalling] = useState(false);
    const [error, setError] = useState("");
    const [alreadyInstalled, setAlreadyInstalled] = useState(installed);

    const install = () => {
        setInstalling(true);
        request("store/?id=" + id, "PUT", {}, {}, false).then(async (r) => {
            setInstalling(false);

            if (r.status === 409) return setAlreadyInstalled(true);
            if (!r.ok) return setError((await r.json()).error || t("plugins.not_supported"));

            updatePlugins();
            closeStore();
        });
    }

    return (
        <Box backgroundColor="background.darker" borderRadius={2} padding={2} sx={{mr: 1, mt: 1, width: {xs: "100%", lg: 300}}}>
            <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between"}}>
                <Typography variant="h6" fontWeight={500}>{name}</Typography>

                <Tooltip title={t("plugins.view_resource")}>
                    <Link href={"https://www.spigotmc.org/resources/" + id} alt="icon" target="_blank">
                        <Box component="img" sx={{width: 40, height: 40, borderRadius: 50}}
                             src={icon ? ("data:image/png;base64," + icon) : ResourceIcon} rel="noreferrer"/>
                    </Link>
                </Tooltip>
            </Box>

            <Typography variant="body1">{description || t("plugins.no_description")}</Typography>

            <Stack direction="row" justifyContent="space-between" sx={{mt: 1}}>
                <Stack direction="row" alignItems="center" gap={0.5}>
                    <Download color="secondary"/>
                    <Typography variant="h6" fontWeight={500}>{prettyDownloadCount(downloads)}</Typography>
                </Stack>
                <Stack direction="row" alignItems="center" gap={1.5}>
                    {installing && <CircularProgress size={20} color="secondary" />}
                    {error !== "" && <Tooltip title={error}><Warning color="error" /></Tooltip>}

                    {alreadyInstalled && <Tooltip title={t("plugins.already_installed")}><Check color="success" /></Tooltip>}

                    <Button variant="contained" color="secondary" size="small" onClick={install}
                            disabled={installing || installed !== undefined}>{t("plugins.install")}</Button>
                </Stack>
            </Stack>
        </Box>
    )
}