import {Box, Button, ButtonGroup, Chip, IconButton, Stack, Tooltip, Typography} from "@mui/material";
import React, {useContext, useState} from "react";
import PlayerActionDialog from "@/states/Root/pages/Players/components/PlayerActionDialog";
import PlayerTable from "@/states/Root/pages/Players/components/PlayerTable";
import {BanListContext} from "./contexts/BanList";
import {PlayerContext} from "@contexts/Players";
import {deleteRequest} from "@/common/utils/RequestUtil.js";
import BanListTable from "@/states/Root/pages/Players/components/BanListTable";
import {WhiteListContext} from "@/states/Root/pages/Players/contexts/WhiteList";
import WhiteListTable from "@/states/Root/pages/Players/components/WhiteListTable";
import {PowerSettingsNew} from "@mui/icons-material";
import {WhitelistAddDialog} from "@/states/Root/pages/Players/components/WhiteListAddDialog/WhitelistAddDialog.jsx";
import {t} from "i18next";

export const Players = () => {
    const {bannedPlayers, updatePlayers} = useContext(BanListContext);
    const {
        whitelistActive,
        switchWhitelist,
        whitelistedPlayers,
        updatePlayers: updateWhitelisted
    } = useContext(WhiteListContext);
    const {players} = useContext(PlayerContext);

    const [selectedPlayers, setSelectedPlayers] = useState([]);
    const [selectedBannedPlayers, setSelectedBannedPlayers] = useState([]);
    const [selectedWhitelistedPlayers, setSelectedWhitelistedPlayers] = useState([]);

    const [whitelistDialogOpen, setWhitelistDialogOpen] = useState(false);

    const [dialogOpen, setDialogOpen] = useState(false);
    const [dialogAction, setDialogAction] = useState("");

    const handleAction = (action) => {
        if (selectedPlayers.length === 0) return;
        setDialogAction(action);
        setDialogOpen(true);
    }

    const removeWhitelistedPlayers = async () => {
        for (const playerId of selectedWhitelistedPlayers) await deleteRequest(`players/whitelist/`,
            {username: whitelistedPlayers.find((player) => player?.uuid === playerId).name});

        setSelectedWhitelistedPlayers([]);
        updateWhitelisted();
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

            <Stack sx={{mt: 2, maxWidth: "100%"}} spacing={2}>
                <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
                    <Typography variant="h5" fontWeight={500}>{t("players.online")} <Chip label={players.length}
                                                                                   color="secondary"/></Typography>
                    <ButtonGroup>
                        <Button color="warning" onClick={() => handleAction("kick")}
                                variant="contained">{t("players.action.kick")}</Button>
                        <Button color="error" onClick={() => handleAction("ban")}
                                variant="contained" >{t("players.action.ban")}</Button>
                    </ButtonGroup>
                </Box>

                <PlayerTable setSelectedPlayers={setSelectedPlayers}/>

                <Stack sx={{mt: 2, flexDirection: {xs: "column", lg: "row"}, justifyContent: "space-between"}} gap={2}>

                    <Stack spacing={2} direction="column" sx={{width: {xs: "100%", lg: "48%"}}}>
                        <Box
                            sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
                            <Typography variant="h5" fontWeight={500}>{t("players.whitelisted_players")} <Chip
                                label={whitelistedPlayers.length}
                                color="secondary"/></Typography>

                            <Stack direction="row" spacing={2}>
                                <Tooltip title={whitelistActive ? t("players.disable_whitelist") : t("players.enable_whitelist")}>
                                    <IconButton color={whitelistActive ? "success" : "error"} onClick={switchWhitelist}>
                                        <PowerSettingsNew/>
                                    </IconButton>
                                </Tooltip>
                                <ButtonGroup>
                                    <Button color="primary" variant="contained" onClick={() =>
                                        setWhitelistDialogOpen(true)}>{t("action.add")}</Button>
                                    <Button color="error" variant="contained"
                                            onClick={removeWhitelistedPlayers}>{t("action.remove")}</Button>
                                </ButtonGroup>
                            </Stack>
                        </Box>

                        <WhitelistAddDialog open={whitelistDialogOpen} setOpen={setWhitelistDialogOpen}/>

                        <WhiteListTable setSelectedWhitelistedPlayers={setSelectedWhitelistedPlayers}/>
                    </Stack>


                    <Stack spacing={2} direction="column" sx={{width: {xs: "100%", lg: "48%"}}}>
                        <Box
                            sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
                            <Typography variant="h5" fontWeight={500}>{t("players.banned_players")} <Chip label={bannedPlayers.length}
                                                                                           color="secondary"/></Typography>
                            <Button color="primary" onClick={unbanPlayers} variant="contained">{t("players.unban")}</Button>
                        </Box>

                        <BanListTable setSelectedBannedPlayers={setSelectedBannedPlayers}/>
                    </Stack>
                </Stack>
            </Stack>
        </>
    );

}