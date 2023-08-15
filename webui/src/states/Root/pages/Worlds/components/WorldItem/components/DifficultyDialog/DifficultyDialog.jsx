import {
    Alert,
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    MenuItem,
    Select,
    Snackbar,
} from "@mui/material";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {WorldsContext} from "@/states/Root/pages/Worlds/contexts/Worlds";
import {useContext, useState} from "react";
import {t} from "i18next";

export const DifficultyDialog = ({open, setOpen, difficulty, name}) => {

    const {updateWorlds} = useContext(WorldsContext);
    const [snackbarOpen, setSnackbarOpen] = useState(false);

    const [value, setValue] = useState(difficulty.toLowerCase());

    const changeDifficulty = (difficulty) => {
        patchRequest("worlds/difficulty", {world: name, difficulty}).then(() => {
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
                    {t("worlds.difficulty.changed")}
                </Alert>
            </Snackbar>
            <Dialog open={open} onClose={() => setOpen(null)}>
                <Box component="form" noValidate>
                    <DialogTitle>{t("worlds.difficulty.title")}</DialogTitle>
                    <DialogContent>
                        <Select label={t("worlds.world_type")} fullWidth variant="standard" value={value}
                                onChange={(e) => setValue(e.target.value)}>
                            <MenuItem value="peaceful">{t("worlds.difficulty.peaceful")}</MenuItem>
                            <MenuItem value="easy">{t("worlds.difficulty.easy")}</MenuItem>
                            <MenuItem value="normal">{t("worlds.difficulty.normal")}</MenuItem>
                            <MenuItem value="hard">{t("worlds.difficulty.hard")}</MenuItem>
                        </Select>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                        <Button onClick={() => changeDifficulty(value)}>{t("worlds.change")}</Button>
                    </DialogActions>
                </Box>
            </Dialog>
        </>
    );
}