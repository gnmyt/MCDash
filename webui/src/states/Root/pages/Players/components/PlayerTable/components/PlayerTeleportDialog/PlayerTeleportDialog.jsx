import {Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Select, Stack} from "@mui/material";
import {useContext, useEffect, useState} from "react";
import {jsonRequest, postRequest} from "@/common/utils/RequestUtil.js";
import {mapName} from "@/states/Root/pages/Worlds/components/WorldItem/utils.js";
import {PlayerContext} from "@contexts/Players";
import OverworldImage from "@/common/assets/images/overworld.webp";
import NetherImage from "@/common/assets/images/nether.webp";
import EndImage from "@/common/assets/images/end.webp";
import {t} from "i18next";

export const PlayerTeleportDialog = ({open, setOpen, player}) => {

    const [worlds, setWorlds] = useState(null);
    const {updatePlayers} = useContext(PlayerContext);

    const [value, setValue] = useState("");

    useEffect(() => {
        jsonRequest("worlds/").then((data) => setWorlds(data));
    }, [open]);

    useEffect(() => {
        setValue(player?.current_world);
    }, [player]);

    const execute = async () => {
        await postRequest("players/tp/", {username: player.name, world: value});
        setOpen(false);
        updatePlayers();
    }

    if (worlds === null) return <></>;
    if (!value) return <></>;

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{t("players.teleport_title")}</DialogTitle>
            <DialogContent>
                <Select value={value} onChange={(e) => setValue(e.target.value)} fullWidth>
                    {worlds.map((world) => (
                        <MenuItem key={world.name} value={world.name}>
                            <Stack direction={"row"} gap={1}>
                            <img src={world.environment === "NETHER" ? NetherImage : world.environment === "THE_END" ? EndImage : OverworldImage} alt={world.name} width={24} height={24} />
                            {mapName(world.name)}
                            </Stack>
                        </MenuItem>
                    ))}
                </Select>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                <Button onClick={execute}>{t("players.teleport")}</Button>
            </DialogActions>
        </Dialog>
    )
}