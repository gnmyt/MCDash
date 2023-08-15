import {useContext, useEffect, useState} from "react";
import {jsonRequest} from "@/common/utils/RequestUtil.js";
import StoreItem from "@/states/Root/pages/Plugins/components/PluginStore/components/StoreItem";
import {Stack, Typography} from "@mui/material";
import {PluginsContext} from "@/states/Root/pages/Plugins/contexts/Plugins";
import {t} from "i18next";

export const PluginStore = ({search, closeStore}) => {
    const [plugins, setPlugins] = useState([]);
    const [pluginOffset, setPluginOffset] = useState(1);

    const {plugins: currentPlugins} = useContext(PluginsContext);

    const loadPlugins = (reset = false) => {
        jsonRequest("store/?page=" + pluginOffset + (search ? "&query=" + search : "")).then((data) => setPlugins(current => {
            if (reset) return data;
            const ids = current.map((p) => p.id);
            const newPlugins = data.filter((p) => !ids.includes(p.id));

            return [...current, ...newPlugins];
        }));
    }

    useEffect(() => {
        loadPlugins();

        const loadWithOffset = () => {
            if (window.innerHeight + window.scrollY >= document.body.offsetHeight)
                setPluginOffset(current => current + 1);
        }

        window.addEventListener("scroll", loadWithOffset);

        return () => window.removeEventListener("scroll", loadWithOffset);
    }, []);

    useEffect(() => {
        loadPlugins();
    }, [pluginOffset]);

    useEffect(() => {
        setPluginOffset(1);
        loadPlugins(true);
    }, [search]);

    return (
        <>
            <Stack direction="row" sx={{my: 1, alignItems: "baseline"}} flexWrap="wrap">
                {plugins.map((plugin) => <StoreItem {...plugin} key={plugin.id} closeStore={closeStore}
                                                    installed={currentPlugins.find((p) => p.path
                                                        ?.startsWith("Managed-" + plugin.id + ".jar"))} />)}

                {plugins.length === 0 && <Typography sx={{width: "100%"}} textAlign="center">{t("plugins.no_plugins")}</Typography>}
            </Stack>
        </>
    )

}