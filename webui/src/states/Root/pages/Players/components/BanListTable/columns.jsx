import {Typography} from "@mui/material";
import {t} from "i18next";

const columns = () => [
    {
        field: 'name', headerName: t("players.username"), minWidth: 150, flex: 1, renderCell: (params) => {
            return (
                <div style={{display: "flex", alignItems: "center"}}>
                    <img src={`https://crafatar.com/avatars/${params.row.uuid}?size=25&overlay`} alt={params.row.name}
                         style={{marginRight: 5}}/>
                    <Typography>{params.row.name}</Typography>
                </div>
            )
        }
    },
    {field: 'uuid', headerName: t("players.id"), flex: 1, minWidth: 200},
    {field: 'reason', headerName: t("players.reason"), flex: 1, minWidth: 150},
    {
        field: 'last_seen', headerName: t("players.last_seen"), flex: 1, minWidth: 100, renderCell: (params) =>
            (<Typography>{new Date(params.row.last_seen).toLocaleString()}</Typography>)
    }
];

export default columns;