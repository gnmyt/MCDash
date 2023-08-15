import {
    Box,
    Button,
    Chip,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Stack,
    TextField
} from "@mui/material";
import {PlayerContext} from "@contexts/Players";
import {useContext, useState} from "react";
import {capitalizeFirst} from "@/common/utils/StringUtil.js";
import {postRequest, putRequest} from "@/common/utils/RequestUtil.js";
import {BanListContext} from "@/states/Root/pages/Players/contexts/BanList";
import {t} from "i18next";

export const PlayerActionDialog = ({open, action, setOpen, selected}) => {
    const {players, updatePlayers} = useContext(PlayerContext);
    const {updatePlayers: updateBanList} = useContext(BanListContext);
    const [reason, setReason] = useState("");

    const executeAction = async (event) => {
        if (event) event.preventDefault();
        setReason("");

        for (const playerId of selected)
            await (action === "kick" ? postRequest : putRequest)(`players/${action === "kick" ? "kick" : "banlist"}/`,
                {reason: reason || t("players.action.kicked"), username: players.find((p) => p.uuid === playerId).name});

        setOpen(false);
        updatePlayers();
        updateBanList();
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <Box component="form" noValidate onSubmit={executeAction}>
                <DialogTitle>{t(`players.action_player`, {action: t(`players.action.${action}`),
                    plural: selected.length > 1 ? "s" : ""})}</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {t("players.action_player_text", {action: t(`players.action.${action}`).toLowerCase(),
                            plural: selected.length > 1 ? "s" : ""})}
                    </DialogContentText>

                    <Stack direction="row" spacing={1} style={{marginTop: "1rem", marginBottom: "0.5rem"}}>
                        {selected.map((player, index) => (
                            <Chip key={index} label={players.find((p) => p.uuid === player)?.name}/>
                        ))}
                    </Stack>

                    <TextField autoFocus label={t("players.reason")} fullWidth variant="standard" value={reason}
                               onChange={(e) => setReason(e.target.value)}/>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                    <Button type="submit" onClick={executeAction}>{t(`players.action.${action}`)}</Button>
                </DialogActions>
            </Box>
        </Dialog>
    );
}