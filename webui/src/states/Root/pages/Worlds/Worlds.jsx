import {Box, Button, Stack, Typography} from "@mui/material";
import React, {useContext, useState} from "react";
import {WorldsContext} from "@/states/Root/pages/Worlds/contexts/Worlds";

import {Add} from "@mui/icons-material";
import CreateWorldDialog from "@/states/Root/pages/Worlds/components/CreateWorldDialog";
import WorldItem from "@/states/Root/pages/Worlds/components/WorldItem";
import {t} from "i18next";

export const Worlds = () => {

    const [createWorldDialogOpen, setCreateWorldDialogOpen] = useState(false);
    const {worlds} = useContext(WorldsContext);

    return (
        <>
            <CreateWorldDialog open={createWorldDialogOpen} setOpen={setCreateWorldDialogOpen}/>
            <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
                <Typography variant="h5" fontWeight={500}>{t("nav.worlds")}</Typography>

                <Stack direction="row" spacing={1}>
                    <Button variant="outlined" color="secondary" startIcon={<Add/>}
                            onClick={() => setCreateWorldDialogOpen(true)}>{t("worlds.create")}</Button>
                </Stack>
            </Box>

            <Stack direction="row" sx={{my: 1, alignItems: "baseline"}} flexWrap="wrap">
                {worlds.map((world) => (<WorldItem {...world} key={world.name}/>))}
            </Stack>

        </>
    );
}