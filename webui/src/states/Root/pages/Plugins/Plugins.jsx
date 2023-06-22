import {Stack, Typography} from "@mui/material";
import React, {useContext} from "react";
import {PluginsContext} from "@/states/Root/pages/Plugins/contexts/Plugins";
import {PluginItem} from "@/states/Root/pages/Plugins/components/PluginItem/PluginItem.jsx";

export const Plugins = () => {

    const {plugins} = useContext(PluginsContext);

    return (
        <>
            <Typography variant="h5" fontWeight={500}>Plugins</Typography>

            <Stack direction="row" sx={{my: 1, alignItems: "baseline"}} flexWrap="wrap">
                {plugins.map((plugin) => <PluginItem key={plugin.name} {...plugin} />)}
            </Stack>
        </>
    )
}