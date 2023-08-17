import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    TextField
} from "@mui/material";
import {useContext, useEffect, useState} from "react";
import {WhiteListContext} from "@/states/Root/pages/Players/contexts/WhiteList";
import {putRequest} from "@/common/utils/RequestUtil.js";
import {t} from "i18next";

export const WhitelistAddDialog = ({open, setOpen}) => {
    const {updatePlayers} = useContext(WhiteListContext);

    const [playerName, setPlayerName] = useState("");

    useEffect(() => {
        if (!open) setPlayerName("");
    }, [open]);

    const executeAction = async (event) => {
        if (event) event.preventDefault();

        putRequest(`players/whitelist`, {username: playerName})
            .then(() => updatePlayers());

        setOpen(false);
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <Box component="form" noValidate onSubmit={executeAction}>
                <DialogTitle>{t("players.whitelist_title")}</DialogTitle>
                <DialogContent>
                    <TextField autoFocus label={t("players.name")} fullWidth variant="standard" value={playerName}
                               onChange={(e) => setPlayerName(e.target.value)}/>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                    <Button type="submit" onClick={executeAction}>{t("action.add")}</Button>
                </DialogActions>
            </Box>
        </Dialog>
    );
}