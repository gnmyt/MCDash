import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    MenuItem,
    Select,
    Stack,
    TextField
} from "@mui/material";
import {useContext, useEffect, useState} from "react";
import {WorldsContext} from "@/states/Root/pages/Worlds/contexts/Worlds";
import {putRequest} from "@/common/utils/RequestUtil.js";
import {t} from "i18next";

export const CreateWorldDialog = ({open, setOpen}) => {
    const {updateWorlds} = useContext(WorldsContext);
    const [name, setName] = useState("");
    const [environment, setEnvironment] = useState("normal");

    const create = (event) => {
        event.preventDefault();

        putRequest("worlds/", {name, environment}).then(() => {
            updateWorlds();
            setOpen(false);
        });
    }

    useEffect(() => {
        setName("");
        setEnvironment("normal");
    }, [open]);

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <Box component="form" noValidate onSubmit={create}>
                <DialogTitle>{t("worlds.create_title")}</DialogTitle>
                <DialogContent>
                    <Stack direction="column" gap={2}>
                        <TextField autoFocus label={t("worlds.world_name")} fullWidth variant="standard" value={name}
                                   onChange={(e) => setName(e.target.value)}/>

                        <Select label={t("worlds.world_type")} fullWidth variant="standard" value={environment}
                                onChange={(e) => setEnvironment(e.target.value)}>
                            <MenuItem value="normal">{t("worlds.overworld")}</MenuItem>
                            <MenuItem value="nether">{t("worlds.nether")}</MenuItem>
                            <MenuItem value="the_end">{t("worlds.end")}</MenuItem>
                        </Select>
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                    <Button type="submit" disabled={name.length === 0}>{t("action.create")}</Button>
                </DialogActions>
            </Box>
        </Dialog>
    )
}