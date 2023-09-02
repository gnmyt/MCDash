import {Stack, Typography, useMediaQuery, useTheme} from "@mui/material";
import {t} from "i18next";

export const ServerDown = () => {
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("lg"));

    return (
        <Stack direction="row" alignItems="center" justifyContent="center" sx={{height: "100vh"}} gap={8} p={3}>
            {!isMobile && <img src="/assets/img/favicon.png" alt="MCDash logo" height={200}/>}

            <Stack direction="column" justifyContent="center" sx={{width: "32rem"}} gap={2}>
                <Typography variant="h3" fontWeight={700}>{t("info.down.title")}</Typography>

                <Typography variant="h5" fontWeight={500}>
                    {t("info.down.description")}
                </Typography>
            </Stack>
        </Stack>
    );
}