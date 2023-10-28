import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, Select, Stack} from "@mui/material";
import {useContext, useState} from "react";
import {SettingsContext} from "@contexts/Settings";
import GermanFlag from "@/common/assets/images/flags/de.webp";
import EnglishFlag from "@/common/assets/images/flags/en.webp";
import SpainFlag from "@/common/assets/images/flags/es.webp";
import FrenchFlag from "@/common/assets/images/flags/fr.webp";
import JapaneseFlag from "@/common/assets/images/flags/ja.webp";
import PolishFlag from "@/common/assets/images/flags/pl.webp";
import {t} from "i18next";

export const ChangeLanguageDialog = ({open, setOpen}) => {

    const {language, updateLanguage} = useContext(SettingsContext);

    const flags = [
        {code: "en", flag: EnglishFlag, name: "English"},
        {code: "de", flag: GermanFlag, name: "Deutsch"},
        {code: "es", flag: SpainFlag, name: "Español"},
        {code: "fr", flag: FrenchFlag, name: "Français"},
        {code: "ja", flag: JapaneseFlag, name: "日本語"},
        {code: "pl", flag: PolishFlag, name: "Polski"}
    ]

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
                    {flags.map((flag) => (
                        <MenuItem key={flag.code} value={flag.code}>
                            <Stack direction={"row"} gap={1} alignItems={"center"}>
                                <Box component="img" src={flag.flag} height={12} alt={flag.name} borderRadius={0.2}/>
                                {flag.name}
                            </Stack>
                        </MenuItem>
                    ))}
                </Select>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpen(false)}>{t("action.cancel")}</Button>
                <Button onClick={save}>{t("action.save")}</Button>
            </DialogActions>
        </Dialog>
    );
}