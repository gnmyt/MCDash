import {Typography} from "@mui/material";
import {capitalizeFirst} from "@/common/utils/StringUtil.js";
import {t} from "i18next";

export const formatWorld = (world) => {
    if (world === "world") return <Typography>{t("worlds.overworld")}</Typography>;
    if (world === "world_nether") return <Typography color="error">{t("worlds.nether")}</Typography>;
    if (world === "world_the_end") return <Typography color="indigo">{t("worlds.end")}</Typography>;
    return <Typography>{capitalizeFirst(world)}</Typography>;
}

export const formatTime = (ticks) => {
    const totalSeconds = Math.floor(ticks / 20);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
}