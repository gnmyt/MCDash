package de.gnm.voxeldash.api.store;

public class StoreSearchResult {
    
    private final StoreProject[] projects;
    private final int totalHits;
    private final int page;
    private final int pageSize;
    
    public StoreSearchResult(StoreProject[] projects, int totalHits, int page, int pageSize) {
        this.projects = projects;
        this.totalHits = totalHits;
        this.page = page;
        this.pageSize = pageSize;
    }
    
    public StoreProject[] getProjects() {
        return projects;
    }
    
    public int getTotalHits() {
        return totalHits;
    }
    
    public int getPage() {
        return page;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public int getTotalPages() {
        return (int) Math.ceil((double) totalHits / pageSize);
    }
}
