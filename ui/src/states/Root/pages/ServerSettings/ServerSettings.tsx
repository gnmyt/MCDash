import {useEffect, useState} from "react"
import {WarningCircleIcon, GearSixIcon} from "@phosphor-icons/react"
import {parsePropertyValue} from "@/lib/PropertyUtil.ts"
import {propertyMappings} from "@/states/Root/pages/ServerSettings/property-mappings.ts"
import {ServerProperty} from "@/types/config"
import {PropertyCard} from "@/states/Root/pages/ServerSettings/components/PropertyCard.tsx";
import {jsonRequest, patchRequest} from "@/lib/RequestUtil.ts";
import {Alert, AlertDescription, AlertTitle} from "@/components/ui/alert.tsx";
import {t} from "i18next";
import {toast} from "@/hooks/use-toast.ts";


const ServerSettings = () => {
    const [properties, setProperties] = useState<ServerProperty[] | null>(null);

    const handleValueChange = (name: string, value: string) => {
        setProperties((prev) => {
            if (!prev) return null;
            return prev.map((prop) => prop.name === name ? {...prop, value} : prop);
        });

        patchRequest("properties/" + name, {value}).then(() => {
            toast({title: t("properties.updated_title"), description: t("properties.updated_description")});
        });
    }

    const parsedProperties = properties?.map((prop) => {
        const mapping = propertyMappings.find((m) => m.name === prop.name);
        if (!mapping) return parsePropertyValue(prop, {name: prop.name, icon: GearSixIcon, type: "string"});
        return parsePropertyValue(prop, mapping)
    });

    useEffect(() => {
        jsonRequest("properties").then((data) => {
            if (!data) return;

            setProperties(Object.keys(data.properties).map((key) => {
                return {name: key, value: data.properties[key]};
            }));
        });
    }, []);

    if (!parsedProperties) return null;

    return (
        <div className="p-6 bg-background min-h-screen">
            <div className="max-w-7xl mx-auto space-y-6">
                <Alert variant="destructive">
                    <WarningCircleIcon className="h-4 w-4"/>
                    <AlertTitle>{t("action.warn")}</AlertTitle>
                    <AlertDescription>
                        {t("properties.warning")}
                    </AlertDescription>
                </Alert>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {parsedProperties.map((property) => (
                        <PropertyCard key={property.name} property={property} onValueChange={handleValueChange}/>
                    ))}
                </div>
            </div>
        </div>
    )
}

export default ServerSettings;