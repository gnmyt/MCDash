import {useContext} from "react";
import {WhiteListContext} from "@/states/Root/pages/Players/contexts/WhiteList";
import {DataGrid} from "@mui/x-data-grid";
import columns from "./columns.jsx";
import {Stack} from "@mui/material";
import {t} from "i18next";

export const WhiteListTable = ({setSelectedWhitelistedPlayers}) => {
    const {whitelistedPlayers} = useContext(WhiteListContext);

    return (
        <>
            <DataGrid
                rows={whitelistedPlayers.map((player) => ({id: player?.uuid, ...player}))}
                columns={columns()}
                initialState={{pagination: {paginationModel: {page: 0, pageSize: 10}}}}
                pageSizeOptions={[10, 25, 50]}
                checkboxSelection
                disableColumnFilter={true}
                disableColumnMenu={true}
                onRowSelectionModelChange={(newSelection) => {
                    setSelectedWhitelistedPlayers(newSelection);
                }}
                sx={{display: 'grid', gridTemplateRows: 'auto 1f auto'}}
                autoHeight={true}
                slots={{
                    noRowsOverlay: () => <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                        {t("players.none.whitelisted")}
                    </Stack>
                }}
                slotProps={{pagination: {labelRowsPerPage: t("players.per_page")}}}
            />
        </>
    );
}