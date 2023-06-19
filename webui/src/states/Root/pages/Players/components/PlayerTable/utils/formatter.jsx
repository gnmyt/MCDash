import {Typography} from "@mui/material";
import {capitalizeFirst} from "@/common/utils/StringUtil.js";

export const formatWorld = (world) => {
    if (world === "world") return <Typography>Overworld</Typography>;
    if (world === "world_nether") return <Typography color="error">Nether</Typography>;
    if (world === "world_the_end") return <Typography color="indigo">The End</Typography>;
    return <Typography>{capitalizeFirst(world)}</Typography>;
}

export const formatTime = (ticks) => {
    const totalSeconds = Math.floor(ticks / 20);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
}