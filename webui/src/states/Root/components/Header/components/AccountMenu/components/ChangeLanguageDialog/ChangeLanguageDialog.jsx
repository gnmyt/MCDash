import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Select, Stack} from "@mui/material";
import {useContext, useState} from "react";
import {SettingsContext} from "@contexts/Settings";
import GermanFlag from "@/common/assets/images/flags/de.webp";
import EnglishFlag from "@/common/assets/images/flags/en.webp";
import SpainFlag from "@/common/assets/images/flags/es.webp";
import {t} from "i18next";

export const ChangeLanguageDialog = ({open, setOpen}) => {

    const {language, updateLanguage} = useContext(SettingsContext);

    const [value, setValue] = useState(language);

    const save = () => {
        updateLanguage(value);
        setOpen(false);
    }

    return (
        <Dialog open={open} onClose={() => setOpen(false)}>
            <DialogTitle>{(t("header.update_language"))}</DialogTitle>
            <DialogContent>
                <Select fullWidth value={value} onChange={(e) => setValue(e.target.value)} variant="outlined">
                    <MenuItem value="en">
                        <Stack direction={"row"} gap={1} alignItems={"center"}>
                            <Box component="img" src={EnglishFlag} height={12} alt="English" borderRadius={0.2}/>
                            English
                        </Stack>
                    </MenuItem>
                    <MenuItem value="de">
                        <Stack direction={"row"} gap={1} alignItems={"center"}>
                            <Box component="img" src={GermanFlag} height={12} alt="English" borderRadius={0.2}/>
                            Deutsch
                        </Stack>
                    </MenuItem>
                    <MenuItem value="es">
                        <Stack direction={"row"} gap={1} alignItems={"center"}>
                            <Box component="img" src={SpainFlag} height={12} alt="Spanish" borderRadius={0.2}/>
                            Español
                        </Stack>
                    </MenuItem>
                </Select>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                <Button onClick={save}>{t("action.save")}</Button>
            </DialogActions>
        </Dialog>
    );
}