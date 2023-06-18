import {useContext, useState} from "react";
import {TokenContext} from "@contexts/Token/index.js";
import {Navigate} from "react-router-dom";
import {Alert, Box, Button, Container, Grid, Stack, TextField, Typography} from "@mui/material";

export const Login = () => {
    const {tokenValid, checkToken} = useContext(TokenContext);
    const [token, setToken] = useState("");
    const [loginFailed, setLoginFailed] = useState(false);

    const login = (e) => {
        if (e) e.preventDefault();
        localStorage.setItem("token", token);
        checkToken().then((r) => setLoginFailed(!r));
    }

    return (
        <div>
            {tokenValid && <Navigate to={"/"}/>}

            <Container maxWidth="xs">
                <Grid container spacing={0} direction="column" justifyContent="center" style={{minHeight: "100vh"}}>
                    <Box sx={{
                        boxShadow: 5, borderRadius: 2, py: 4, display: "flex", flexDirection: "column",
                        alignItems: "center", justifyContent: "center"
                    }}>
                        <Stack direction="row" alignItems="center" gap={1}>
                            <img src="/assets/img/favicon.png" alt="MCDash" width="40px" height="40px" />
                            <Typography variant="h5" noWrap>Sign In</Typography>
                        </Stack>
                        {loginFailed && <Alert severity="error" sx={{mt: 1, width: "80%"}}>
                            The provided token is invalid.
                        </Alert>}
                        <Box component="form" noValidate sx={{mt: 1}} onSubmit={login}>
                            <TextField margin="normal" value={token} required fullWidth label="Your token"
                                       type="password" autoFocus onChange={(e) => setToken(e.target.value)}/>
                            <Button variant="contained" fullWidth sx={{mt: 3}} onClick={login}>
                                Sign In
                            </Button>
                        </Box>
                    </Box>
                </Grid>
            </Container>
        </div>
    );
};
