import {Box, Chip, IconButton, Typography} from "@mui/material";
import {Close} from "@mui/icons-material";

export const FileHeader = ({currentFile, directory, setDirectory, setCurrentFile}) => {
    return (
        <Box sx={{display: "flex", alignItems: "center", justifyContent: "space-between", mt: 2, mb: 2}}>
            <Typography variant="h5" fontWeight={500}>File Manager
                {currentFile === null && directory.split("/").splice(0, directory.split("/").length - 1).map((dir, index) => (
                    <Chip key={index} label={dir || "/"} color="secondary" style={{marginLeft: 5}}
                          onClick={() => setDirectory(directory.substring(0, directory.indexOf(dir) + dir.length + 1))}/>
                ))}
            </Typography>

            {currentFile !== null && <IconButton color="secondary" onClick={() => setCurrentFile(null)}><Close /></IconButton>}
        </Box>
    );
}