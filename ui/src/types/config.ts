import {type LucideIcon} from 'lucide-react'

export interface ServerProperty {
    name: string
    value: string
}

export interface PropertyMapping {
    name: string
    icon: LucideIcon
    type: 'boolean' | 'number' | 'string' | 'json' | 'gamemode' | 'difficulty'
    options?: string[]
}

export interface ParsedProperty extends ServerProperty {
    mapping: PropertyMapping
    parsedValue: boolean | number | string
}

