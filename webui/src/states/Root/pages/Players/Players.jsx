import {Box, Button, ButtonGroup, Chip, Typography} from "@mui/material";
import {useContext, useState} from "react";
import PlayerActionDialog from "@/states/Root/pages/Players/components/PlayerActionDialog/index.js";
import PlayerTable from "@/states/Root/pages/Players/components/PlayerTable/index.js";
import {BanListContext} from "@/states/Root/pages/Players/components/PlayerTable/contexts/BanList/index.js";
import {PlayerContext} from "@contexts/Players/index.js";
import {deleteRequest} from "@/common/utils/RequestUtil.js";
import BanListTable from "@/states/Root/pages/Players/components/BanListTable";

export const Players = () => {
    const {bannedPlayers, updatePlayers} = useContext(BanListContext);
    const {players} = useContext(PlayerContext);
    const [selectedPlayers, setSelectedPlayers] = useState([]);
    const [selectedBannedPlayers, setSelectedBannedPlayers] = useState([]);

    const [dialogOpen, setDialogOpen] = useState(false);
    const [dialogAction, setDialogAction] = useState("");

    const handleAction = (action) => {
        if (selectedPlayers.length === 0) return;
        setDialogAction(action);
        setDialogOpen(true);
    }

    const unbanPlayers = async () => {
        for (const playerId of selectedBannedPlayers) await deleteRequest(`players/banlist/`,
                {username: bannedPlayers.find((p) => p.uuid === playerId).name});

        setSelectedBannedPlayers([]);
        updatePlayers();
    }

    return (
        <>
            <PlayerActionDialog action={dialogAction} open={dialogOpen} setOpen={setDialogOpen}
                                selected={selectedPlayers}/>

            <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
                <Typography variant="h5" fontWeight={500}>Online Players <Chip label={players.length}
                                                                               color="secondary"/></Typography>
                <ButtonGroup>
                    <Button color="warning" onClick={() => handleAction("kick")} variant="contained">Kick</Button>
                    <Button color="error" variant="contained" onClick={() => handleAction("ban")}>Ban</Button>
                </ButtonGroup>
            </Box>

            <PlayerTable setSelectedPlayers={setSelectedPlayers}/>

            {bannedPlayers.length > 0 && <>
                <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
                    <Typography variant="h5" fontWeight={500}>Banned Players <Chip label={bannedPlayers.length} color="secondary"/></Typography>
                    <Button color="primary" onClick={unbanPlayers} variant="contained">Unban</Button>
                </Box>

                <BanListTable setSelectedBannedPlayers={setSelectedBannedPlayers} />
            </>}
        </>
    );

}