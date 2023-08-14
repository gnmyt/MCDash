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
    Snackbar,
} from "@mui/material";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {WorldsContext} from "@/states/Root/pages/Worlds/contexts/Worlds";
import {useContext, useState} from "react";

export const DifficultyDialog = ({open, setOpen, difficulty, name}) => {

    const {updateWorlds} = useContext(WorldsContext);
    const [snackbarOpen, setSnackbarOpen] = useState(false);

    const [value, setValue] = useState(difficulty.toLowerCase());

    const changeDifficulty = (difficulty) => {
        patchRequest("worlds/difficulty", {world: name, difficulty}).then(() => {
            updateWorlds();
            setOpen(null);
            setSnackbarOpen(true);
        });
    }

    return (
        <>
            <Snackbar open={snackbarOpen} autoHideDuration={3000} onClose={() => setSnackbarOpen(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert onClose={() => setSnackbarOpen(false)} severity={"success"} sx={{width: '100%'}}>
                    Difficulty changed successfully!
                </Alert>
            </Snackbar>
            <Dialog open={open} onClose={() => setOpen(null)}>
                <Box component="form" noValidate>
                    <DialogTitle>Change difficulty</DialogTitle>
                    <DialogContent>
                        <Select label="Type of the world" fullWidth variant="standard" value={value}
                                onChange={(e) => setValue(e.target.value)}>
                            <MenuItem value="peaceful">Peaceful</MenuItem>
                            <MenuItem value="easy">Easy</MenuItem>
                            <MenuItem value="normal">Normal</MenuItem>
                            <MenuItem value="hard">Hard</MenuItem>
                        </Select>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpen(false)}>Cancel</Button>
                        <Button onClick={() => changeDifficulty(value)}>Change</Button>
                    </DialogActions>
                </Box>
            </Dialog>
        </>
    );
}