package de.gnm.mcdash.api.store;

public class StoreFile {
    
    private final String url;
    private final String filename;
    private final boolean primary;
    private final long size;
    private final String sha512;
    
    public StoreFile(String url, String filename, boolean primary, long size, String sha512) {
        this.url = url;
        this.filename = filename;
        this.primary = primary;
        this.size = size;
        this.sha512 = sha512;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public boolean isPrimary() {
        return primary;
    }
    
    public long getSize() {
        return size;
    }
    
    public String getSha512() {
        return sha512;
    }
}
