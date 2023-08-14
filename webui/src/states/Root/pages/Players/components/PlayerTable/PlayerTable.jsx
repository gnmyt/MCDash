import {DataGrid} from "@mui/x-data-grid";
import columns from "./columns.jsx";
import {useContext, useState} from "react";
import {PlayerContext} from "@contexts/Players";
import {deleteRequest, putRequest} from "@/common/utils/RequestUtil.js";
import ActionConfirmDialog from "@components/ActionConfirmDialog";
import {Alert, Snackbar} from "@mui/material";

export const PlayerTable = ({setSelectedPlayers}) => {
    const {players, updatePlayers} = useContext(PlayerContext);

    const [actionFinished, setActionFinished] = useState(false);
    const [opWarning, setOPWarning] = useState(false);
    const [opPlayer, setOPPlayer] = useState(null);

    const setOP = async (player) => {
        if (player.is_op) {
            updateOpStatus(player);
            setActionFinished(true);
            return;
        }

        setOPPlayer(player);
        setOPWarning(true);
    }

    const updateOpStatus = (player) => {
        (player.is_op ? deleteRequest : putRequest)("/players/op/", {username: player.name})
            .then(() => updatePlayers());
    }

    const confirm = () => {
        setOPWarning(false);
        updateOpStatus(opPlayer);

        return true;
    }

    return (
        <>
            <ActionConfirmDialog open={opWarning} setOpen={setOPWarning} title={"Warning"} onClick={confirm}
                                 description={"The player will be granted operator permissions. Are you sure?"} />

            <Snackbar open={actionFinished} autoHideDuration={3000} onClose={() => setActionFinished(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setActionFinished(false)} severity={"success"} sx={{width: '100%'}}>
                    Action executed successfully
                </Alert>
            </Snackbar>

            <DataGrid
                rows={players?.map((player) => ({...player, id: player?.uuid}))}
                columns={columns({setOP})}
                initialState={{pagination: {paginationModel: { page: 0, pageSize: 10 }}}}
                pageSizeOptions={[10, 25, 50]}
                checkboxSelection
                disableColumnFilter={true}
                disableColumnMenu={true}
                onRowSelectionModelChange={(newSelection) => {setSelectedPlayers(newSelection)}}
                sx={{display: 'grid', gridTemplateRows: 'auto 1f auto'}}
                autoHeight={true}
            />
        </>
    );
}