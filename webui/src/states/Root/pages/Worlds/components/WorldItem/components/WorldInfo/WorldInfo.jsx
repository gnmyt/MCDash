import {Stack, Typography} from "@mui/material";
import {Cloud, Gavel, Group, Timelapse} from "@mui/icons-material";
import {mapTime} from "@/states/Root/pages/Worlds/components/WorldItem/utils.js";
import {capitalizeFirst} from "@/common/utils/StringUtil.js";
import {t} from "i18next";

export const WorldInfo = ({players, time, weather, difficulty}) => {
    return (
        <Stack direction="row" sx={{mt: 1.5, mb: 2}} flexWrap="wrap">
            <Stack direction="row" alignItems="center" gap={0.5} width="50%">
                <Group color="secondary"/>
                <Typography variant="h6" fontWeight={500}>{t("worlds.info.players")}: <Typography component="span"
                                                                               color="secondary">{players}</Typography></Typography>
            </Stack>
            <Stack direction="row" alignItems="center" gap={0.5} width="50%">
                <Timelapse color="secondary"/>
                <Typography variant="h6" fontWeight={500}>{t("worlds.info.time")}: <Typography component="span"
                                                                            color="secondary">{mapTime(time)}</Typography></Typography>
            </Stack>

            <Stack direction="row" alignItems="center" gap={0.5} width="50%">
                <Cloud color="secondary"/>
                <Typography variant="h6" fontWeight={500}>{t("worlds.info.weather")}: <Typography component="span"
                                                                               color="secondary">{capitalizeFirst(weather)}</Typography></Typography>
            </Stack>

            <Stack direction="row" alignItems="center" gap={0.5} width="50%">
                <Gavel color="secondary"/>
                <Typography variant="h6" fontWeight={500}>{t("worlds.info.difficulty")}: <Typography component="span"
                                                                                  color="secondary">{capitalizeFirst(difficulty)}</Typography></Typography>
            </Stack>
        </Stack>
    )
}