import {Alert, Box, Snackbar, Stack, Switch, TextField} from "@mui/material";
import {Settings} from "@mui/icons-material";
import React, {useState} from "react";
import {patchRequest} from "@/common/utils/RequestUtil.js";
import {capitalizeFirst} from "@/common/utils/StringUtil.js";
import {t} from "i18next";

export const ConfigurationItem = ({property, updateProperties}) => {

    const [value, setValue] = useState(property.value === "true" || property.value === "false" ? property.value === "true" : property.value);

    const [changesSaved, setChangesSaved] = useState(false);

    const newChanges = () => {
        updateProperties();
        if (!changesSaved) {
            setChangesSaved(true);

            setTimeout(() => setChangesSaved(false), 3000);
        }
    }

    const updateProperty = (event) => setValue(event.target.value);

    const updateSwitch = (event) => {
        setValue(event.target.checked)
        patchRequest("manage/property", {name: property.name, value: event.target.checked ? "true" : "false"})
            .then(newChanges);
    }

    const rewritePropertyName = () => {
        let newName = "";
        property.name.split("-").forEach((word) => newName += " " + capitalizeFirst(word));
        return newName;
    }

    const saveProperty = () => {
        if (value === property.value) return;
        patchRequest("manage/property", {name: property.name, value: value}).then(newChanges);
    }

    return (
        <>
            <Snackbar open={changesSaved} message={t("action.changes_saved")} onClick={() => setChangesSaved(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert severity="success" sx={{width: "100%"}}>{t("configuration.saved")}</Alert>
            </Snackbar>

            <Box key={property.name} backgroundColor="background.darker" borderRadius={2} padding={2}
                 sx={{mt: 1, mr: 1, display: "flex", justifyContent: "space-between", alignItems: "center",
                    width: {xs: "100%", lg: "calc(50% - 8px)"}}}
                 flexWrap="wrap">
                <Stack direction="row" spacing={1} alignItems="center">
                    <Settings/>
                    <h2>{rewritePropertyName()}</h2>
                </Stack>
                {property.value === "true" || property.value === "false" ? (
                    <Switch checked={value} onChange={updateSwitch}/>) : (
                    <TextField label={property.name} variant="outlined" value={value}
                               type={property.value === "0" || parseInt(property.value) ? "number" : "text"}
                               onChange={updateProperty} onBlur={saveProperty}/>
                )}
            </Box>
        </>

    )
}