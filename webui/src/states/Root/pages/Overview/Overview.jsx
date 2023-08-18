import {Box, Stack, Typography} from "@mui/material";
import {Label, Settings, SettingsEthernet} from "@mui/icons-material";
import {jsonRequest} from "@/common/utils/RequestUtil.js";
import {useEffect, useState} from "react";
import StatisticBox from "@/states/Root/pages/Overview/components/StatisticBox";
import OverviewArea from "@/states/Root/pages/Overview/components/OverviewArea";
import {t} from "i18next";
import WelcomeTip from "@/states/Root/pages/Overview/components/WelcomeTip";

export const Overview = () => {
    const [welcomeShown, setWelcomeShown] = useState(localStorage.getItem("welcomeShown") === "true" || false);
    const [serverInfo, setServerInfo] = useState(null);

    const handleWelcomeClose = () => {
        setWelcomeShown(true);
        localStorage.setItem("welcomeShown", "true");
    }

    const getMotd = () => {
        return serverInfo.motd.split("\n").map((line, index) => {
            line = line.replace(/§[0-9a-fk-or]/g, "");
            return <Typography key={index} variant="span"
                               fontWeight={500}>{line.length > 25 ? line.substring(0, 25) + "..." : line}<br/></Typography>
        });
    }

    useEffect(() => {
        jsonRequest("server").then((r) => setServerInfo(r));
    }, []);

    return (
        <>
            <OverviewArea/>
            <Stack direction={{xs: "column", lg: "row"}} justifyContent="space-between" sx={{mt: 3}} gap={5}
                   alignItems={{xs: "stretch", lg: "flex-start"}}>
                <Stack direction="column" gap={2} width={{xs: "100%", lg: "78.5%"}}>

                    <Typography variant="h5" fontWeight={500}>{t("overview.tip.title")}</Typography>

                    {!welcomeShown && <WelcomeTip handleWelcomeClose={handleWelcomeClose}/>}
                    {welcomeShown && <Typography variant="body1" fontWeight={500} sx={{color: "text.secondary"}}>
                        {t("overview.tip.none")}
                    </Typography>}
                </Stack>
                {serverInfo && <Stack direction="column" gap={1} sx={{flexGrow: 1}}>
                    <StatisticBox title={t("overview.software")} value={serverInfo.software} icon={<Settings/>}
                                   color="success"/>
                    <StatisticBox title={t("overview.version")} value={serverInfo.version} icon={<Label/>} color="success"/>
                    <StatisticBox title={t("overview.ip")}
                                   value={serverInfo.ip + (serverInfo.port !== 25565 ? ":" + serverInfo.port : "")}
                                   icon={<SettingsEthernet/>} color="success"/>
                    <StatisticBox value={getMotd()} icon={<Box component="img" src={serverInfo.icon}/>}
                                   color="success"/>
                </Stack>}
            </Stack>


        </>

    )
}