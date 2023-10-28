import {DataGrid} from "@mui/x-data-grid";
import columns from "./columns.jsx";
import {useContext, useState} from "react";
import {PlayerContext} from "@contexts/Players";
import {deleteRequest, putRequest} from "@/common/utils/RequestUtil.js";
import ActionConfirmDialog from "@components/ActionConfirmDialog";
import {Alert, Snackbar, Stack} from "@mui/material";
import PlayerTeleportDialog
    from "@/states/Root/pages/Players/components/PlayerTable/components/PlayerTeleportDialog";
import {t} from "i18next";

export const PlayerTable = ({setSelectedPlayers}) => {
    const {players, updatePlayers} = useContext(PlayerContext);

    const [actionFinished, setActionFinished] = useState(false);

    const [opWarning, setOPWarning] = useState(false);
    const [teleportDialog, setTeleportDialog] = useState(false);

    const [currentPlayer, setCurrentPlayer] = useState(null);

    const teleportPlayer = (player) => {
        setCurrentPlayer(player);
        setTeleportDialog(true);
    }

    const setOP = async (player) => {
        if (player.is_op) {
            updateOpStatus(player);
            setActionFinished(true);
            return;
        }

        setCurrentPlayer(player);
        setOPWarning(true);
    }

    const updateOpStatus = (player) => {
        (player.is_op ? deleteRequest : putRequest)("players/op/", {username: player.name})
            .then(() => updatePlayers());
    }

    const confirm = () => {
        setOPWarning(false);
        updateOpStatus(currentPlayer);

        return true;
    }

    return (
        <>
            <ActionConfirmDialog open={opWarning} setOpen={setOPWarning} title={t("action.warn")} onClick={confirm}
                                 description={t("players.op_player")} />

            <PlayerTeleportDialog open={teleportDialog} setOpen={setTeleportDialog} player={currentPlayer} />

            <Snackbar open={actionFinished} autoHideDuration={3000} onClose={() => setActionFinished(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setActionFinished(false)} severity={"success"} sx={{width: '100%'}}>
                    {t("action.success")}
                </Alert>
            </Snackbar>

            <DataGrid
                rows={players?.map((player) => ({...player, id: player?.uuid}))}
                columns={columns({setOP, teleportPlayer})}
                initialState={{pagination: {paginationModel: { page: 0, pageSize: 10 }}}}
                pageSizeOptions={[10, 25, 50]}
                checkboxSelection
                disableColumnFilter={true}
                disableColumnMenu={true}
                onRowSelectionModelChange={(newSelection) => {setSelectedPlayers(newSelection)}}
                sx={{display: 'grid', gridTemplateRows: 'auto 1f auto'}}
                autoHeight={true}
                slots={{
                    noRowsOverlay: () => <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                        {t("players.none.online")}
                    </Stack>
                }}
                slotProps={{pagination: {labelRowsPerPage: t("players.per_page")}}}
            />
        </>
    );
}