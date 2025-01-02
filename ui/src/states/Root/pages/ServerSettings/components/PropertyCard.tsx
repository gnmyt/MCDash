import {useState} from 'react'
import {Card, CardHeader, CardTitle, CardDescription, CardContent} from '@/components/ui/card'
import {Switch} from '@/components/ui/switch'
import {Input} from '@/components/ui/input'
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from '@/components/ui/select'
import {ParsedProperty} from '@/types/config'
import {formatPropertyName} from '@/lib/PropertyUtil.ts'
import {exists, t} from "i18next";

interface PropertyCardProps {
    property: ParsedProperty;
    onValueChange: (name: string, value: string) => void;
}

export function PropertyCard({property, onValueChange}: PropertyCardProps) {
    const [value, setValue] = useState(property.value);
    const Icon = property.mapping.icon;

    const handleChange = (newValue: string) => {
        setValue(newValue);
        onValueChange(property.name, newValue);
    }

    const renderInput = () => {
        switch (property.mapping.type) {
            case 'boolean':
                return (
                    <div className="flex items-center justify-between w-full">
                        <Switch checked={property.parsedValue as boolean}
                                onCheckedChange={(checked) => handleChange(checked.toString())}/>
                        <span className="text-sm text-muted-foreground">
                            {(property.parsedValue as boolean) ? 'Enabled' : 'Disabled'}
                        </span>
                    </div>
                )
            case 'number':
                return (
                    <Input type="number" value={value}
                           onChange={(e) => handleChange(e.target.value)} className="w-full"/>
                )
            case 'gamemode':
            case 'difficulty':
                return (
                    <Select value={value} onValueChange={handleChange}>
                        <SelectTrigger><SelectValue/></SelectTrigger>
                        <SelectContent>
                            {property.mapping.options?.map((option) => (
                                <SelectItem key={option} value={option}>
                                    {option.charAt(0).toUpperCase() + option.slice(1)}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                )
            case 'json':
                return (
                    <Input value={value} onChange={(e) => handleChange(e.target.value)}
                           className="font-mono w-full"/>
                )
            default:
                return (
                    <Input value={value} onChange={(e) => handleChange(e.target.value)}
                           className="w-full"/>
                )
        }
    }

    return (
        <Card className="w-full">
            <div className="flex flex-col h-full">
                <CardHeader className="pb-2">
                    <div className="flex items-center gap-2">
                        <Icon className="h-5 w-5 text-muted-foreground"/>
                        <div>
                            <CardTitle className="text-sm font-medium">
                                {formatPropertyName(property.name)}
                            </CardTitle>
                            <CardDescription className="text-xs">
                                {exists("properties." + property.name) ? t("properties." + property.name) : "Server property"}
                            </CardDescription>
                        </div>
                    </div>
                </CardHeader>
                <CardContent
                    className={`flex-grow flex items-center ${property.mapping.type !== 'boolean' ? 'pt-0' : ''}`}>
                    {renderInput()}
                </CardContent>
            </div>
        </Card>
    )
}

