import {Alert, Box, Button, CircularProgress, Snackbar, Stack, Typography} from "@mui/material";
import React, {useContext, useState} from "react";
import {BackupContext} from "./contexts/Backups";
import BackupItem from "@/states/Root/pages/Backups/components/BackupItem";
import {Add} from "@mui/icons-material";
import BackupCreationDialog from "@/states/Root/pages/Backups/components/BackupCreationDialog";
import {t} from "i18next";

export const Backups = () => {
    const {backups} = useContext(BackupContext);
    const [deleteSnackbarOpen, setDeleteSnackbarOpen] = useState(false);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [loading, setLoading] = useState(false);

    return (
        <>
            <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
                <Stack direction="row" gap={1} alignItems="center">
                    <Typography variant="h5" fontWeight={500}>{t("nav.backups")}</Typography>
                    {loading && <CircularProgress size={15} color="secondary"/>}
                </Stack>

                <Button variant="outlined" color="secondary" startIcon={<Add/>}
                        onClick={() => setDialogOpen(true)}>{t("action.create")}</Button>
            </Box>

            <BackupCreationDialog open={dialogOpen} setOpen={setDialogOpen} setLoading={setLoading}/>

            <Snackbar open={deleteSnackbarOpen} autoHideDuration={5000} onClose={() => setDeleteSnackbarOpen(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setDeleteSnackbarOpen(false)} severity="success" sx={{width: '100%'}}>
                    {t("backup.delete.success")}
                </Alert>
            </Snackbar>

            <Stack direction="row" sx={{alignItems: "baseline"}} flexWrap="wrap">
                {backups.map((backup, index) => <BackupItem key={backup.id} size={backup.size} modes={backup.modes}
                                                            created={new Date(backup.id)} tag={backups.length - index}
                                                            setDelete={setDeleteSnackbarOpen} id={backup.id}
                                                            latest={index === 0}/>)}

                {backups.length === 0 && <Typography sx={{width: "100%"}} textAlign="center">{t("backup.none_found")}</Typography>}
            </Stack>
        </>
    );
}