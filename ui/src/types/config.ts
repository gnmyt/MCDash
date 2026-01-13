import {type Icon} from '@phosphor-icons/react'

export interface ServerProperty {
    name: string
    value: string
}

export type PropertyCategory = 'gameplay' | 'world' | 'spawning' | 'players' | 'network' | 'performance' | 'security' | 'rcon' | 'resourcepack' | 'info' | 'advanced'

export interface PropertyMapping {
    name: string
    icon: Icon
    type: 'boolean' | 'number' | 'string' | 'json' | 'gamemode' | 'difficulty'
    options?: string[]
    category?: PropertyCategory
}

export interface ParsedProperty extends ServerProperty {
    mapping: PropertyMapping
    parsedValue: boolean | number | string
}

