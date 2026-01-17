export interface World {
    name: string;
    environment: string;
    playerCount: number;
    time: number;
    weather: string;
    difficulty: string;
    seed: number;
    hardcore: boolean;
    worldType: string;
}

export type EnvironmentType = 'NORMAL' | 'NETHER' | 'THE_END';
export type WorldTypeOption = 'NORMAL' | 'FLAT' | 'AMPLIFIED' | 'LARGE_BIOMES';

export interface CreateWorldRequest {
    worldName: string;
    environment: EnvironmentType;
    worldType?: WorldTypeOption;
    seed?: string;
}
