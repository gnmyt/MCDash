import {Box, Button, ButtonGroup, IconButton, Stack} from "@mui/material";
import {Cloud, Delete, Gavel, Timelapse} from "@mui/icons-material";
import WorldHeader from "@/states/Root/pages/Worlds/components/WorldItem/components/WorldHeader";
import WorldInfo from "@/states/Root/pages/Worlds/components/WorldItem/components/WorldInfo";
import {useContext, useState} from "react";
import WeatherDialog from "@/states/Root/pages/Worlds/components/WorldItem/components/WeatherDialog";
import TimeDialog from "@/states/Root/pages/Worlds/components/WorldItem/components/TimeDialog";
import ActionConfirmDialog from "@components/ActionConfirmDialog";
import {deleteRequest} from "@/common/utils/RequestUtil.js";
import {WorldsContext} from "@/states/Root/pages/Worlds/contexts/Worlds";
import DifficultyDialog from "@/states/Root/pages/Worlds/components/WorldItem/components/DifficultyDialog";
import {t} from "i18next";

export const WorldItem = ({name, environment, time, weather, difficulty, players}) => {

    const {updateWorlds} = useContext(WorldsContext);
    const [openDialog, setOpenDialog] = useState(null);

    const deleteWorld = async () => {
        if (players !== 0) return false;
        const request = await deleteRequest("worlds/", {name});

        if (request.status === 200) {
            updateWorlds();
            setOpenDialog(null);
        }

        return request.status === 200;
    }

    return (
        <Box key={name} backgroundColor="background.darker" borderRadius={2} padding={2}
             sx={{mr: 0.8, mt: 1, width: {xs: "100%", lg: 401}}}>

            <WeatherDialog open={openDialog === "weather"} setOpen={setOpenDialog} name={name} weather={weather}/>
            <TimeDialog open={openDialog === "time"} setOpen={setOpenDialog} name={name} time={time}/>
            <DifficultyDialog open={openDialog === "difficulty"} setOpen={setOpenDialog} name={name} difficulty={difficulty}/>
            <ActionConfirmDialog open={openDialog === "delete"} setOpen={setOpenDialog} title={t("worlds.delete.title")}
                                    description={t("worlds.delete.text")} onClick={deleteWorld}
                                    successMessage={t("worlds.delete.success")} buttonText={t("worlds.delete.yes")} />

            <WorldHeader name={name} environment={environment}/>

            <WorldInfo players={players} time={time} weather={weather} difficulty={difficulty}/>

            <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{mt: 1}} gap={1}>
                <ButtonGroup variant="contained" color="secondary" fullWidth size="small">
                    <Button onClick={() => setOpenDialog("weather")}
                            startIcon={<Cloud/>}>{t("worlds.weather.button")}</Button>
                    <Button onClick={() => setOpenDialog("time")}
                            startIcon={<Timelapse/>}>{t("worlds.time.button")}</Button>
                    <Button onClick={() => setOpenDialog("difficulty")}
                            startIcon={<Gavel/>}>{t("worlds.difficulty.button")}</Button>
                </ButtonGroup>
                <IconButton size="small" color="error" onClick={() => setOpenDialog("delete")}
                            disabled={players !== 0 || name === "world"}><Delete/></IconButton>
            </Stack>

        </Box>
    )
}