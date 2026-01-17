package de.gnm.voxeldash.api.store;

import java.io.File;

public class StoreDownloadResult {
    
    private final boolean success;
    private final String error;
    private final File downloadedFile;
    private final String fileName;
    private final String projectId;
    private final String versionId;
    
    private StoreDownloadResult(boolean success, String error, File downloadedFile, 
                                String fileName, String projectId, String versionId) {
        this.success = success;
        this.error = error;
        this.downloadedFile = downloadedFile;
        this.fileName = fileName;
        this.projectId = projectId;
        this.versionId = versionId;
    }
    
    public static StoreDownloadResult success(File downloadedFile, String fileName, 
                                               String projectId, String versionId) {
        return new StoreDownloadResult(true, null, downloadedFile, fileName, projectId, versionId);
    }
    
    public static StoreDownloadResult failure(String error) {
        return new StoreDownloadResult(false, error, null, null, null, null);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getError() {
        return error;
    }
    
    public File getDownloadedFile() {
        return downloadedFile;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public String getVersionId() {
        return versionId;
    }
}
