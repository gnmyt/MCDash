export interface StoreProvider {
    id: string;
    name: string;
    logoPath: string;
}

export interface StoreProject {
    id: string;
    slug: string;
    name: string;
    description: string;
    author: string;
    iconUrl: string | null;
    downloads: number;
    gameVersions: string[];
    projectType: string;
    dateCreated: string;
    dateModified: string;
    latestVersion: string | null;
}

export interface StoreSearchResult {
    totalHits: number;
    page: number;
    pageSize: number;
    totalPages: number;
    gameVersion: string | null;
    loader: string | null;
    projects: StoreProject[];
}

export interface StoreFile {
    url: string;
    filename: string;
    primary: boolean;
    size: number;
}

export interface StoreVersion {
    id: string;
    projectId: string;
    name: string;
    versionNumber: string;
    changelog: string | null;
    gameVersions: string[];
    loaders: string[];
    versionType: "release" | "beta" | "alpha";
    downloads: number;
    datePublished: string;
    files: StoreFile[];
}

export interface InstalledStoreResource {
    provider: string;
    projectId: string;
    fileName: string;
    enabled: boolean;
}