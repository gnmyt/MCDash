import {Box, Chip, IconButton, Stack, Typography} from "@mui/material";
import React, {useContext, useState} from "react";
import {convertSize} from "@/states/Root/pages/Files/components/FileView/utils/FileUtil.js";
import {Delete, Download, Restore} from "@mui/icons-material";
import {deleteRequest, downloadRequest} from "@/common/utils/RequestUtil.js";
import {BackupContext} from "@/states/Root/pages/Backups/contexts/Backups";
import ActionConfirmDialog from "@components/ActionConfirmDialog";
import RestoreDialog from "@/states/Root/pages/Backups/components/BackupItem/components/RestoreDialog";
import mappings from "./mappings.jsx";
import {t} from "i18next";

export const BackupItem = ({id, created, size, latest, tag, modes, setDelete}) => {

    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [restoreDialogOpen, setRestoreDialogOpen] = useState(false);

    const {updateBackups} = useContext(BackupContext);

    const deleteBackup = () => {
        deleteRequest("backups/", {backup_id: id}).then(() => {
            updateBackups();
            setDelete(true);
        });
    }

    const download = () => {
        downloadRequest("backups/download?backup_id=" + id);
    }

    return (
        <>
            <ActionConfirmDialog open={deleteDialogOpen} setOpen={setDeleteDialogOpen} title={t("backup.delete.title")}
                                 description={t("backup.delete.text")} buttonText={t("backup.delete.yes")}
                                 onClick={deleteBackup} successMessage={t("backup.delete.success")} />

            <RestoreDialog id={id} open={restoreDialogOpen} setOpen={setRestoreDialogOpen} />

            <Box backgroundColor="background.darker" borderRadius={2} padding={2} sx={{mr: 1, mt: 1, width: {xs: "100%", lg: "49%"}}}>
                <Stack sx={{flexDirection: "row", justifyContent: "space-between", alignItems: "center"}}>
                    <Stack>
                        <Stack sx={{flexDirection: "row", alignItems: "center"}} gap={0.5}>
                            <Typography variant="h6" fontWeight={500}>{latest ? t("backup.latest") : t("backup.name")} {(!latest && "#"+tag)}</Typography>
                            {modes.map((mode) => <Stack key={mode}>{mappings()[mode]}</Stack>)}
                            <Chip label={convertSize(size)} size="small" color="secondary"/>
                        </Stack>

                        <Typography variant="body2" color="text.secondary">{t("backup.time")} {created.toLocaleString()}</Typography>
                    </Stack>
                    <Box>
                        <IconButton size="small" onClick={() => setRestoreDialogOpen(true)}><Restore /></IconButton>
                        <IconButton size="small" onClick={download}><Download /></IconButton>
                        <IconButton color="error" size="small" onClick={() => setDeleteDialogOpen(true)}><Delete /></IconButton>
                    </Box>
                </Stack>
            </Box>
        </>
    );

}