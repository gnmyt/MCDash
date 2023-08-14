import {Box, Typography} from "@mui/material";
import {mapName} from "@/states/Root/pages/Worlds/components/WorldItem/utils.js";
import NetherImage from "@/common/assets/images/nether.webp";
import EndImage from "@/common/assets/images/end.webp";
import OverworldImage from "@/common/assets/images/overworld.webp";

export const WorldHeader = ({environment, name}) => {
    return (
        <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between"}}>
            <Typography variant="h6" fontWeight={500}>{mapName(name)}</Typography>

            <Box src={environment === "NETHER" ? NetherImage : environment === "THE_END" ? EndImage : OverworldImage}
                 component="img" sx={{width: 40, height: 40}} rel="noreferrer"/>
        </Box>
    );
}