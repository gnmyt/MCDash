export interface User {
    id: number;
    username: string;
    isAdmin: boolean;
    permissions: UserPermissions;
}

export interface UserPermissions {
    [feature: string]: PermissionLevel;
}

export type PermissionLevel = 0 | 1 | 2;
