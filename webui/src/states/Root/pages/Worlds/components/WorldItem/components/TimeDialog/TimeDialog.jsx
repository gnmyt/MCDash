import {Alert, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Slider, Snackbar,} from "@mui/material";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {WorldsContext} from "@/states/Root/pages/Worlds/contexts/Worlds";
import {useContext, useState} from "react";
import {mapTime, timeMarks} from "@/states/Root/pages/Worlds/components/WorldItem/utils.js";
import {t} from "i18next";

export const TimeDialog = ({open, setOpen, time, name}) => {

    const {updateWorlds} = useContext(WorldsContext);
    const [snackbarOpen, setSnackbarOpen] = useState(false);

    const [value, setValue] = useState(time);

    const changeTime = (time) => {
        patchRequest("worlds/time", {world: name, time}).then(() => {
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
                    {t("worlds.time.changed")}
                </Alert>
            </Snackbar>
            <Dialog open={open} onClose={() => setOpen(null)}>
                <Box component="form" noValidate>
                    <DialogTitle>{t("worlds.time.title")}</DialogTitle>
                    <DialogContent sx={{width: {xs: 300, sm: 400}}}>
                        <Slider defaultValue={time} value={value} onChange={(e, v) => setValue(v)} min={0} max={24000}
                                sx={{mt: 2}}
                                step={100} marks={timeMarks()}/>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(null)}>{t("action.cancel")}</Button>
                        <Button onClick={() => changeTime(value)}>{t("worlds.time.update")} {mapTime(value)}</Button>
                    </DialogActions>
                </Box>
            </Dialog>
        </>
    );
}