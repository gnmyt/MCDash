import {Box, Link, Stack, Tab, Tabs, Typography} from "@mui/material";
import {useState} from "react";

import Icon from "/assets/img/favicon.png";
import ExistingServer from "./pages/ExistingServer";
import NewServer from "./pages/NewServer";
import {SocketProvider} from "@/common/contexts/SocketContext";

const App = () => {
    const [tab, setTab] = useState(0);

    return (
        <>
            <Stack direction="column" justifyContent="center" alignItems="center" sx={{width: 1, height: "100vh"}} gap={2}>

                <Stack direction="row" justifyContent="center" alignItems="center" spacing={1}>
                    <Box component="img" src={Icon} sx={{width: "3.5rem", height: "auto"}}/>
                    <Typography variant="h4" fontWeight={500}>MCDash Creator</Typography>
                </Stack>

                <Box sx={{maxWidth: "26rem"}} backgroundColor="background.darker"
                     padding={2} borderRadius={2}>

                    <Tabs value={tab} onChange={(e, v) => setTab(v)} sx={{width: 1, mb: 2}} variant="fullWidth">
                        <Tab label="Create new server"/>
                        <Tab label="Use existing server"/>
                    </Tabs>

                    <SocketProvider>
                        {tab === 0 && <NewServer />}
                        {tab === 1 && <ExistingServer />}
                    </SocketProvider>
                </Box>

                <Stack direction="row" justifyContent="center" alignItems="center" spacing={1}>
                    <Link href="https://gnmyt.dev/imprint" target="_blank" rel="noopener noreferrer"
                          color="text.secondary" underline="hover" variant="body2">Imprint</Link>
                    <Typography variant="body2" color="text.secondary">|</Typography>
                    <Link href="https://gnmyt.dev/privacy" target="_blank" rel="noopener noreferrer"
                          color="text.secondary" underline="hover" variant="body2">Privacy</Link>
                </Stack>
            </Stack>
        </>
    )
}

export default App;