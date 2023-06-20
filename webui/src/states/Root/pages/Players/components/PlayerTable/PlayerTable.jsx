import {DataGrid} from "@mui/x-data-grid";
import columns from "./columns.jsx";
import {useContext} from "react";
import {PlayerContext} from "@contexts/Players";

export const PlayerTable = ({setSelectedPlayers}) => {
    const {players} = useContext(PlayerContext);

    return (
        <div style={{ height: 500 }}>
            <DataGrid
                rows={players.map((player) => ({...player, id: player?.uuid}))}
                columns={columns}
                initialState={{pagination: {paginationModel: { page: 0, pageSize: 10 }}}}
                pageSizeOptions={[10, 25, 50]}
                checkboxSelection
                disableColumnFilter={true}
                disableColumnMenu={true}
                onRowSelectionModelChange={(newSelection) => {setSelectedPlayers(newSelection)}}
            />
        </div>
    );
}