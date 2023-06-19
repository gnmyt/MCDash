import {useContext} from "react";
import {BanListContext} from "@/states/Root/pages/Players/components/PlayerTable/contexts/BanList/index.js";
import {DataGrid} from "@mui/x-data-grid";
import columns from "./columns.jsx";

export const BanListTable = ({setSelectedBannedPlayers}) => {
    const {bannedPlayers} = useContext(BanListContext);

    return (
        <div style={{height: 500}}>
            <DataGrid
                rows={bannedPlayers.map((player) => ({id: player.uuid, ...player}))}
                columns={columns}
                initialState={{pagination: {paginationModel: {page: 0, pageSize: 10}}}}
                pageSizeOptions={[10, 25, 50]}
                checkboxSelection
                disableColumnFilter={true}
                disableColumnMenu={true}
                onRowSelectionModelChange={(newSelection) => {
                    setSelectedBannedPlayers(newSelection)
                }}
            />
        </div>
    );
}