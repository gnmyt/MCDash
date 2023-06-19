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
import {BanListContext} from "@/states/Root/pages/Players/components/PlayerTable/contexts/BanList/index.js";

export const PlayerActionDialog = ({open, action, setOpen, selected}) => {
    const {players, updatePlayers} = useContext(PlayerContext);
    const {updatePlayers: updateBanList} = useContext(BanListContext);
    const [reason, setReason] = useState("");

    const executeAction = async (event) => {
        if (event) event.preventDefault();
        setReason("");

        for (const playerId of selected)
            await (action === "kick" ? postRequest : putRequest)(`players/${action === "kick" ? "kick" : "banlist"}/`,
                {reason: reason || "You have been kicked!", username: players.find((p) => p.uuid === playerId).name});

        setOpen(false);
        updatePlayers();
        updateBanList();
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <Box component="form" noValidate onSubmit={executeAction}>
                <DialogTitle>{action === "kick" ? "Kick" : "Ban"} Player{selected.length > 1 ? "s" : ""}</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to {action} the following player{selected.length > 1 ? "s" : ""}?
                    </DialogContentText>

                    <Stack direction="row" spacing={1} style={{marginTop: "1rem", marginBottom: "0.5rem"}}>
                        {selected.map((player, index) => (
                            <Chip key={index} label={players.find((p) => p.uuid === player)?.name}/>
                        ))}
                    </Stack>

                    <TextField autoFocus label="Reason" fullWidth variant="standard" value={reason}
                               onChange={(e) => setReason(e.target.value)}/>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>Cancel</Button>
                    <Button type="submit" onClick={executeAction}>{capitalizeFirst(action)}</Button>
                </DialogActions>
            </Box>
        </Dialog>
    );
}