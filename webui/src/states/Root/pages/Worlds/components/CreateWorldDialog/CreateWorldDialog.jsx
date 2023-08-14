import {
    Alert,
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
                <DialogTitle>Create a new world</DialogTitle>
                <DialogContent>
                    <Stack direction="column" gap={2}>
                        <TextField autoFocus label="Name of the world" fullWidth variant="standard" value={name}
                                   onChange={(e) => setName(e.target.value)}/>

                        <Select label="Type of the world" fullWidth variant="standard" value={environment}
                                onChange={(e) => setEnvironment(e.target.value)}>
                            <MenuItem value="normal">Overworld</MenuItem>
                            <MenuItem value="nether">Nether</MenuItem>
                            <MenuItem value="end">End</MenuItem>
                        </Select>
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>Cancel</Button>
                    <Button type="submit" disabled={name.length === 0}>Create</Button>
                </DialogActions>
            </Box>
        </Dialog>
    )
}