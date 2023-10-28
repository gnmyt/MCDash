import {useContext} from "react";
import {BanListContext} from "@/states/Root/pages/Players/contexts/BanList";
import {DataGrid} from "@mui/x-data-grid";
import columns from "./columns.jsx";
import {Stack} from "@mui/material";
import {t} from "i18next";

export const BanListTable = ({setSelectedBannedPlayers}) => {
    const {bannedPlayers} = useContext(BanListContext);

    return (
        <>
            <DataGrid
                rows={bannedPlayers.map((player) => ({id: player.uuid, ...player}))}
                columns={columns()}
                initialState={{pagination: {paginationModel: {page: 0, pageSize: 10}}}}
                pageSizeOptions={[10, 25, 50]}
                checkboxSelection
                disableColumnFilter={true}
                disableColumnMenu={true}
                onRowSelectionModelChange={(newSelection) => {
                    setSelectedBannedPlayers(newSelection)
                }}
                sx={{display: 'grid', gridTemplateRows: 'auto 1f auto'}}
                autoHeight={true}
                slots={{
                    noRowsOverlay: () => <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                        {t("players.none.banned")}
                    </Stack>
                }}
                slotProps={{pagination: {labelRowsPerPage: t("players.per_page")}}}
            />
        </>
    );
}