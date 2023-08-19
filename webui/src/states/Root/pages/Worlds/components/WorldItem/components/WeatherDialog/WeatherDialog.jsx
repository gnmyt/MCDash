import {Alert, Box, Dialog, DialogContent, DialogTitle, IconButton, Snackbar, Stack,} from "@mui/material";
import {Thunderstorm, WaterDrop, WbSunny} from "@mui/icons-material";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {WorldsContext} from "@/states/Root/pages/Worlds/contexts/Worlds";
import {useContext, useState} from "react";
import {t} from "i18next";

export const WeatherDialog = ({open, setOpen, weather, name}) => {

    const {updateWorlds} = useContext(WorldsContext);
    const [snackbarOpen, setSnackbarOpen] = useState(false);

    const changeWeather = (weather) => {
        patchRequest("worlds/weather", {world: name, weather}).then(() => {
            updateWorlds();
            setOpen(null);
            setSnackbarOpen(true);
        });
    }

    return (
        <>
            <Snackbar open={snackbarOpen} autoHideDuration={3000} onClose={() => setSnackbarOpen(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setSnackbarOpen(false)} severity={"success"} sx={{width: '100%'}}>
                    {t("worlds.weather.changed")}
                </Alert>
            </Snackbar>
            <Dialog open={open} onClose={() => setOpen(null)}>
                <Box component="form" noValidate>
                    <DialogTitle>{t("worlds.weather.title")}</DialogTitle>
                    <DialogContent>
                        <Stack direction="row" alignItems="center" justifyContent="center" gap={1}>
                            <IconButton color={weather === "clear" ? "secondary" : "default"}
                                        onClick={() => changeWeather("clear")}><WbSunny/></IconButton>
                            <IconButton color={weather === "rain" ? "secondary" : "default"}
                                        onClick={() => changeWeather("rain")}><WaterDrop/></IconButton>
                            <IconButton color={weather === "thunder" ? "secondary" : "default"}
                                        onClick={() => changeWeather("thunder")}><Thunderstorm/></IconButton>
                        </Stack>
                    </DialogContent>
                </Box>
            </Dialog>
        </>
    );
}