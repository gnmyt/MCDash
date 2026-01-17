import {ServerProperty, PropertyMapping, ParsedProperty} from '../types/config.ts'

export function parsePropertyValue(property: ServerProperty, mapping: PropertyMapping): ParsedProperty {
    let parsedValue: boolean | number | string = property.value;

    switch (mapping.type) {
        case 'boolean':
            parsedValue = property.value.toLowerCase() === 'true';
            break;
        case 'number':
            parsedValue = Number(property.value);
            if (isNaN(parsedValue)) parsedValue = 0;
            break;
        case 'json':
            try {
                JSON.parse(property.value);
                parsedValue = property.value;
            } catch {
                parsedValue = '{}';
            }
            break;
        case 'gamemode':
            if (!mapping.options?.includes(property.value)) {
                parsedValue = mapping.options?.[0] || 'survival';
            }
            break;
    }

    return {...property, mapping, parsedValue};
}

export function formatPropertyName(name: string): string {
    return name
        .split(/[-.]/)
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}