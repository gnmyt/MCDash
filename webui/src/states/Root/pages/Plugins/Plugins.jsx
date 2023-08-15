import {Box, Button, Chip, Stack, TextField, Typography} from "@mui/material";
import React, {useContext, useState} from "react";
import {PluginsContext} from "@/states/Root/pages/Plugins/contexts/Plugins";
import {PluginItem} from "@/states/Root/pages/Plugins/components/PluginItem/PluginItem.jsx";
import {Store} from "@mui/icons-material";
import PluginStore from "@/states/Root/pages/Plugins/components/PluginStore";
import {t} from "i18next";

export const Plugins = () => {
    const [storeOpen, setStoreOpen] = useState(false);
    const {plugins} = useContext(PluginsContext);

    const [search, setSearch] = useState("");
    const [currentSearch, setCurrentSearch] = useState("");

    return (
        <>
            <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
                <Typography variant="h5" fontWeight={500}>{t("nav.plugins")} <Chip label={plugins.length}
                                                                        color="secondary"/></Typography>

                <Stack direction="row" spacing={1}>
                    {storeOpen && <TextField label={t("plugins.search")} variant="outlined" size={"small"} sx={{width: {xs: 150, lg: 300}}}
                            value={search} onChange={(e) => setSearch(e.target.value)} color="secondary"
                                             onKeyUp={(e) => e.key === "Enter" && setCurrentSearch(search)}
                    onBlur={() => setCurrentSearch(search)} />}
                        <Button variant={storeOpen ? "contained" : "outlined"} color="secondary" startIcon={<Store/>}
                        onClick={() => setStoreOpen(open => !open)}>{t("plugins.store")}</Button>
                </Stack>
            </Box>

            {!storeOpen && <Stack direction="row" sx={{my: 1, alignItems: "baseline"}} flexWrap="wrap">
                {plugins.map((plugin) => <PluginItem key={plugin.path} {...plugin} />)}
            </Stack>}

            {storeOpen && <PluginStore search={currentSearch} closeStore={() => setStoreOpen(false)} />}
        </>
    )
}