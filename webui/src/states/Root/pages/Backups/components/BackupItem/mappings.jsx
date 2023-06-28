import {Check, Description, Extension, Public, Settings} from "@mui/icons-material";
import {Tooltip} from "@mui/material";
import React from "react";

export default {
    "0": <Tooltip title="Complete backup"><Check /></Tooltip>,
    "1": <Tooltip title="Worlds"><Public /></Tooltip>,
    "2": <Tooltip title="Plugins"><Extension /></Tooltip>,
    "3": <Tooltip title="Configurations"><Settings /></Tooltip>,
    "4": <Tooltip title="Logs"><Description /></Tooltip>
}