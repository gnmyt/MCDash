import {Box, Button, IconButton, Stack, Tooltip, Typography} from "@mui/material";
import {t} from "i18next";
import {Book, Close, Favorite} from "@mui/icons-material";

export const WelcomeTip = ({handleWelcomeClose}) => {
    return (
        <Box sx={{flex: 1, display: "flex", flexDirection: "column"}}
             backgroundColor="background.darker" borderRadius={1.5} padding={2}>
            <Stack direction="row" alignItems="center" gap={2} justifyContent="space-between">
                <Stack direction="row" alignItems="center" gap={2}>
                    <img src="/assets/img/favicon.png" alt="MCDash" width="40px" height="40px"/>
                    <Typography variant="h5" fontWeight={600}>{t("overview.tip.welcome")}</Typography>
                </Stack>
                <Tooltip title={t("overview.tip.close")}>
                    <IconButton onClick={handleWelcomeClose}><Close/></IconButton>
                </Tooltip>
            </Stack>

            <Typography variant="body1" fontWeight={500} sx={{mt: 1}}>
                {t("overview.tip.text")}<br/>{t("overview.tip.subtext")}
            </Typography>
            <Stack direction="row" gap={1}>
                <Button variant="text" href="https://mcdash.gnmyt.dev/" target="_blank" sx={{mt: 1}}
                        startIcon={<Book/>} color="success">
                    {t("overview.tip.documentation")}
                </Button>
                <Button variant="text" href="https://ko-fi.com/gnmyt" target="_blank" sx={{mt: 1}}
                        startIcon={<Favorite/>} color="error">
                    {t("overview.tip.donate")}
                </Button>
            </Stack>
        </Box>
    );
}