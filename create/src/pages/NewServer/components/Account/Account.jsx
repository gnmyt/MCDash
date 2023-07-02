import {Alert, IconButton, Stack, TextField} from "@mui/material";
import {useState} from "react";
import {Visibility, VisibilityOff} from "@mui/icons-material";

export const Account = ({username, setUsername, password, setPassword}) => {
    const [passwordShown, setPasswordShown] = useState(false);

    return (
        <Stack direction="column" justifyContent="space-between" spacing={2}>
            <Alert severity="warning">Don't enter your Microsoft/Mojang account details here. Those credentials
            are only used to authenticate you with the panel.</Alert>
            <TextField fullWidth label="Your Minecraft name" variant="outlined" value={username} placeholder="Steve"
                       onChange={(e) => setUsername(e.target.value)} />
            <TextField fullWidth label="Your password" variant="outlined" value={password}
                       onChange={(e) => setPassword(e.target.value)} type={passwordShown ? "text" : "password"}
                          InputProps={{
                                endAdornment: <IconButton onClick={() => setPasswordShown(!passwordShown)}>{passwordShown
                                    ? <VisibilityOff/> : <Visibility/>}</IconButton>
                          }}
            />
        </Stack>
    )
}
