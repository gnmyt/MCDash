import {jsonRequest, postRequest} from "@/lib/RequestUtil";
import {
    StoreProvider,
    StoreSearchResult,
    StoreVersion,
    InstalledStoreResource,
} from "@/types/store";

export const getStoreProviders = async (resourceType?: string): Promise<StoreProvider[]> => {
    const params = resourceType ? `?type=${encodeURIComponent(resourceType)}` : "";
    const response = await jsonRequest(`store/providers${params}`);
    return response.providers || [];
};

export const searchStore = async (
    type: string,
    query: string = "",
    page: number = 0,
    pageSize: number = 20,
    provider: string = "modrinth"
): Promise<StoreSearchResult> => {
    const params = new URLSearchParams({
        type,
        query,
        page: page.toString(),
        pageSize: pageSize.toString(),
        provider
    });

    const response = await jsonRequest(`store/search?${params.toString()}`);
    return response.result || {
        totalHits: 0,
        page: 0,
        pageSize: pageSize,
        totalPages: 0,
        gameVersion: null,
        loader: null,
        projects: []
    };
};

export const getProjectVersions = async (
    projectId: string,
    provider: string = "modrinth",
    resourceType?: string
): Promise<{ versions: StoreVersion[]; gameVersion: string | null; loader: string | null }> => {
    const params = new URLSearchParams({
        projectId,
        provider
    });
    if (resourceType) {
        params.append("type", resourceType);
    }

    const response = await jsonRequest(`store/versions?${params.toString()}`);
    return {
        versions: response.versions || [],
        gameVersion: response.gameVersion || null,
        loader: response.loader || null
    };
};

export const installStoreResource = async (
    type: string,
    projectId: string,
    versionId: string,
    provider: string = "modrinth"
): Promise<{ success: boolean; message?: string; error?: string; fileName?: string; enabled?: boolean }> => {
    const response = await postRequest("store/install", {
        type,
        projectId,
        versionId,
        provider
    });

    if (response.error) {
        return {success: false, error: response.error};
    }

    return {
        success: true,
        message: response.message,
        fileName: response.fileName,
        enabled: response.enabled
    };
};

export const getInstalledStoreResources = async (
    type: string
): Promise<InstalledStoreResource[]> => {
    const params = new URLSearchParams({type});
    const response = await jsonRequest(`store/installed?${params.toString()}`);
    return response.installed || [];
};

export const formatDownloads = (count: number): string => {
    if (count >= 1000000) {
        return `${(count / 1000000).toFixed(1)}M`;
    }
    if (count >= 1000) {
        return `${(count / 1000).toFixed(1)}K`;
    }
    return count.toString();
};

export const formatFileSize = (bytes: number): string => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
};

export const formatRelativeTime = (dateString: string): string => {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now.getTime() - date.getTime();

    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const months = Math.floor(days / 30);
    const years = Math.floor(days / 365);

    if (years > 0) return `${years}y ago`;
    if (months > 0) return `${months}mo ago`;
    if (days > 0) return `${days}d ago`;
    if (hours > 0) return `${hours}h ago`;
    if (minutes > 0) return `${minutes}m ago`;
    return "just now";
};
