import {Box, Button, Stack, Typography} from "@mui/material";
import {Folder, InsertDriveFile} from "@mui/icons-material";
import {convertSize} from "./utils/FileUtil.js";
import {t} from "i18next";

export const FileView = ({files, changeDirectory, click, handleContextMenu}) => {
    return (
        <div style={{display: "flex", flexWrap: "wrap", gap: 10, marginTop: 10, flexDirection: "column"}}>
            {files.length === 0 &&
                <Button variant="outlined" color="secondary" onClick={() => changeDirectory("..")}>{t("files.go_back")}</Button>}
            {files.map((file) => (
                <Box key={file.name} display="flex" gap={1} padding={2}
                     onClick={(event) => click(file, event)}
                     backgroundColor={"background.darker"} borderRadius={2.5} style={{cursor: "pointer"}}
                     alignItems="center"
                     onContextMenu={(e) => handleContextMenu(e, file)}>
                    {file.is_folder && <Folder color="primary" style={{cursor: "pointer"}}/>}

                    {!file.is_folder && <InsertDriveFile color="primary"/>}

                    <Typography>{file.name}</Typography>

                    <Stack direction="row" alignItems="center" gap={1} marginLeft="auto">
                        {!file.is_folder && <Typography>{convertSize(file.size)}</Typography>}
                        <Typography>{new Date(file.last_modified).toLocaleString()}</Typography>
                    </Stack>
                </Box>
            ))}
        </div>
    );
}