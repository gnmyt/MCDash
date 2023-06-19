import {Typography} from "@mui/material";

const columns = [
    {
        field: 'name', headerName: 'Username', minWidth: 200, flex: 1, renderCell: (params) => {
            return (
                <div style={{display: "flex", alignItems: "center"}}>
                    <img src={`https://crafatar.com/avatars/${params.row.uuid}?size=25&overlay`} alt={params.row.name}
                         style={{marginRight: 5}}/>
                    <Typography>{params.row.name}</Typography>
                </div>
            )
        }
    },
    {field: 'uuid', headerName: 'Player-ID', flex: 1, minWidth: 300},
    {field: 'reason', headerName: 'Reason', flex: 1, minWidth: 300},
    {
        field: 'last_seen', headerName: 'Last seen', flex: 1, renderCell: (params) =>
            (<Typography>{new Date(params.row.last_seen).toLocaleString()}</Typography>)
    }
];

export default columns;