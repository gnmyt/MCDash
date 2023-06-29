import {Box, Stack, Tab, Tabs, Typography} from "@mui/material";
import {useState} from "react";

import Icon from "/assets/img/favicon.png";
import ExistingServer from "./pages/ExistingServer";
import NewServer from "./pages/NewServer";

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

                    {tab === 0 && <NewServer />}
                    {tab === 1 && <ExistingServer />}
                </Box>
            </Stack>
        </>
    )
}

export default App;