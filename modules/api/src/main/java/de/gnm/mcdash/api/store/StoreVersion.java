package de.gnm.mcdash.api.store;

public class StoreVersion {

    private final String id;
    private final String projectId;
    private final String name;
    private final String versionNumber;
    private final String changelog;
    private final String[] gameVersions;
    private final String[] loaders;
    private final String versionType;
    private final int downloads;
    private final String datePublished;
    private final StoreFile[] files;

    public StoreVersion(String id, String projectId, String name, String versionNumber,
                        String changelog, String[] gameVersions, String[] loaders,
                        String versionType, int downloads, String datePublished, StoreFile[] files) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.versionNumber = versionNumber;
        this.changelog = changelog;
        this.gameVersions = gameVersions;
        this.loaders = loaders;
        this.versionType = versionType;
        this.downloads = downloads;
        this.datePublished = datePublished;
        this.files = files;
    }

    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getChangelog() {
        return changelog;
    }

    public String[] getGameVersions() {
        return gameVersions;
    }

    public String[] getLoaders() {
        return loaders;
    }

    public String getVersionType() {
        return versionType;
    }

    public int getDownloads() {
        return downloads;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public StoreFile[] getFiles() {
        return files;
    }

    /**
     * Gets the primary file for this version
     *
     * @return the primary file or the first file if none is marked as primary
     */
    public StoreFile getPrimaryFile() {
        if (files == null || files.length == 0) return null;
        for (StoreFile file : files) {
            if (file.isPrimary()) return file;
        }
        return files[0];
    }
}
