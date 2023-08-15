import {Check, Description, Extension, Public, Settings} from "@mui/icons-material";
import {Tooltip} from "@mui/material";
import React from "react";
import {t} from "i18next";

export default () => ({
    "0": <Tooltip title={t("backup.mapping.complete")}><Check /></Tooltip>,
    "1": <Tooltip title={t("backup.mapping.worlds")}><Public /></Tooltip>,
    "2": <Tooltip title={t("backup.mapping.plugins")}><Extension /></Tooltip>,
    "3": <Tooltip title={t("backup.mapping.config")}><Settings /></Tooltip>,
    "4": <Tooltip title={t("backup.mapping.logs")}><Description /></Tooltip>
});