import {Checkbox, IconButton, Stack, Tooltip, Typography} from "@mui/material";
import HealthImage from "@/common/assets/images/health.webp";
import FoodImage from "@/common/assets/images/food.webp";
import {capitalizeFirst} from "@/common/utils/StringUtil.js";
import {formatTime, formatWorld} from "./utils/formatter.jsx";
import {Send} from "@mui/icons-material";
import {t} from "i18next";

const columns = ({setOP, teleportPlayer}) => [
    {
        field: 'name', headerName: t("players.username"), minWidth: 200, flex: 1, renderCell: (params) => {
            return (
                <div style={{display: "flex", alignItems: "center"}}>
                    <img src={`https://crafatar.com/avatars/${params.row.uuid}?size=25&overlay`} alt={params.row.name}
                         style={{marginRight: 5}}/>
                    <Typography>{params.row.name}</Typography>
                </div>
            )
        }
    },
    {field: 'uuid', headerName: t("players.id"), flex: 1, minWidth: 300},
    {
        field: 'current_world', headerName: t("players.world"), minWidth: 150, flex: 1, renderCell: (params) => {
            return (
                <Stack direction="row" alignItems="center" gap={0.5}>
                    {formatWorld(params.row.current_world)}
                    <Tooltip title="Send to world"><IconButton size="small" onClick={(e) => {
                        e.stopPropagation();
                        teleportPlayer(params.row);
                    }}><Send/></IconButton></Tooltip>
                </Stack>
            )
        }
    },
    {field: 'address', headerName: t("players.address")},
    {
        field: 'health', headerName: t("players.health"), type: "number", minWidth: 70, flex: 0.5, renderCell: (params) => (
            <div style={{display: "flex", alignItems: "center"}}>
                <Typography>{params.row.health / 2}</Typography>
                <img src={HealthImage} alt="Health" style={{marginLeft: 5}} width={20} height={20}/>
            </div>)
    },
    {
        field: 'food_level', headerName: t("players.food_level"), type: "number", minWidth: 70, flex: 0.5, renderCell: (params) => (
            <div style={{display: "flex", alignItems: "center"}}>
                <Typography>{params.row.food_level / 2}</Typography>
                <img src={FoodImage} alt="Food" style={{marginLeft: 5}} width={20} height={20}/>
            </div>)
    },
    {
        field: 'is_op', headerName: t("players.is_op"), type: "boolean", minWidth: 70, flex: 0.5, renderCell: (params) => (
            <Checkbox checked={params.row.is_op} onChange={() => setOP(params.row)}
                      onClick={(e) => e.stopPropagation()}/>
        )
    },
    {
        field: 'game_mode', headerName: t("players.game_mode"), flex: 1, renderCell: (params) => (
            <Typography>{capitalizeFirst(params.row.game_mode)}</Typography>
        )
    },
    {
        field: 'player_time', headerName: t("players.player_time"), flex: 1, renderCell: (params) => (
            <Typography>{formatTime(params.row.player_time)}</Typography>
        )
    },
];

export default columns;