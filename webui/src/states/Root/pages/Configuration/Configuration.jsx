import React, {useContext} from "react";
import {PropertiesContext} from "@/states/Root/pages/Configuration/contexts/Properties";
import {Stack, Typography} from "@mui/material";
import ConfigurationItem from "@/states/Root/pages/Configuration/components/ConfigurationItem/index.js";

export const Configuration = () => {
    const {properties, updateProperties} = useContext(PropertiesContext);

    return (
        <div>
            <Typography variant="h5" fontWeight={500}>Configuration</Typography>

            <Stack direction="row" sx={{mt: 2}} flexWrap="wrap">
                {properties.map((property) =>
                    <ConfigurationItem property={property} updateProperties={updateProperties} key={property.name}/>
                )}
            </Stack>

        </div>
    )
}