package de.gnm.mcdash.api.store;

public class StoreProject {
    
    private final String id;
    private final String slug;
    private final String name;
    private final String description;
    private final String author;
    private final String iconUrl;
    private final int downloads;
    private final int followers;
    private final String[] categories;
    private final String[] gameVersions;
    private final String projectType;
    private final String dateCreated;
    private final String dateModified;
    private final String latestVersion;
    
    public StoreProject(String id, String slug, String name, String description, String author,
                        String iconUrl, int downloads, int followers, String[] categories,
                        String[] gameVersions, String projectType, String dateCreated,
                        String dateModified, String latestVersion) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.author = author;
        this.iconUrl = iconUrl;
        this.downloads = downloads;
        this.followers = followers;
        this.categories = categories;
        this.gameVersions = gameVersions;
        this.projectType = projectType;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.latestVersion = latestVersion;
    }
    
    public String getId() {
        return id;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public int getDownloads() {
        return downloads;
    }
    
    public int getFollowers() {
        return followers;
    }
    
    public String[] getCategories() {
        return categories;
    }
    
    public String[] getGameVersions() {
        return gameVersions;
    }
    
    public String getProjectType() {
        return projectType;
    }
    
    public String getDateCreated() {
        return dateCreated;
    }
    
    public String getDateModified() {
        return dateModified;
    }
    
    public String getLatestVersion() {
        return latestVersion;
    }
}
