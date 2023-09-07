import {useContext, useState} from "react";
import {TokenContext} from "@contexts/Token";
import {Navigate} from "react-router-dom";
import {Alert, Box, Button, Container, Divider, Grid, Stack, TextField, Typography} from "@mui/material";
import {Buffer} from "buffer";
import {t} from "i18next";

export const Login = () => {
    const {tokenValid, checkToken} = useContext(TokenContext);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loginFailed, setLoginFailed] = useState(false);

    const login = (e) => {
        if (e) e.preventDefault();
        localStorage.setItem("token", Buffer.from(`${username}:${password}`).toString("base64"));
        checkToken().then((r) => setLoginFailed(!r));
    }

    return (
        <div>
            {tokenValid && <Navigate to={"/"}/>}

            <Container maxWidth="xs">
                <Grid container spacing={0} direction="column" justifyContent="center" style={{minHeight: "100vh"}}>
                    <Box sx={{
                        boxShadow: 5, borderRadius: 2, py: 4, display: "flex", flexDirection: "column",
                        alignItems: "center", justifyContent: "center"}}>
                        <Stack direction="row" alignItems="center" gap={1}>
                            <img src="/assets/img/favicon.png" alt="MCDash" width="50px" height="50px" />
                            <Typography variant="h5" noWrap fontWeight={700}>MCDash</Typography>
                            <Divider orientation="vertical" sx={{height: "30px", mx: 0.5}} />
                            <Typography variant="h5" noWrap color="text.secondary">{t("login.sign_in")}</Typography>
                        </Stack>
                        {loginFailed && <Alert severity="error" sx={{mt: 1, width: "80%"}}>
                            {t("login.failed")}
                        </Alert>}
                        <Box component="form" onSubmit={login} sx={{mt: 1, width: "80%"}}>
                            <TextField margin="normal" value={username} required fullWidth label={t("login.name")}
                                        autoFocus onChange={(e) => setUsername(e.target.value)}/>

                            <TextField margin="normal" value={password} required fullWidth label={t("login.password")}
                                        type="password" onChange={(e) => setPassword(e.target.value)}/>

                            <Button variant="contained" fullWidth sx={{mt: 3}} onClick={login} type="submit">
                                {t("login.sign_in")}
                            </Button>
                        </Box>
                    </Box>
                </Grid>
            </Container>
        </div>
    );
};
