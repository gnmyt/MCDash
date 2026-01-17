package de.gnm.voxeldash.api.entities;

public class Resource {

    private final String name;
    private final String fileName;
    private final ResourceType type;
    private final String version;
    private final String description;
    private final String[] authors;
    private final boolean enabled;
    private final String iconPath;
    private final long fileSize;

    public Resource(String name, String fileName, ResourceType type, String version,
                    String description, String[] authors, boolean enabled, String iconPath, long fileSize) {
        this.name = name;
        this.fileName = fileName;
        this.type = type;
        this.version = version;
        this.description = description;
        this.authors = authors;
        this.enabled = enabled;
        this.iconPath = iconPath;
        this.fileSize = fileSize;
    }

    /**
     * Gets the display name of the resource
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the file name of the resource (with or without .disabled suffix)
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the type of this resource
     *
     * @return the resource type
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * Gets the version of the resource
     *
     * @return the version or null if unknown
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the description of the resource
     *
     * @return the description or null if not available
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the authors of the resource
     *
     * @return array of author names
     */
    public String[] getAuthors() {
        return authors;
    }

    /**
     * Whether this resource is currently enabled
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the path to the icon of this resource (if available)
     *
     * @return the icon path or null
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * Gets the file size in bytes
     *
     * @return the file size
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Builder for creating Resource instances
     */
    public static class Builder {
        private String name;
        private String fileName;
        private ResourceType type;
        private String version;
        private String description;
        private String[] authors = new String[0];
        private boolean enabled = true;
        private String iconPath;
        private long fileSize = 0;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder type(ResourceType type) {
            this.type = type;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder authors(String[] authors) {
            this.authors = authors != null ? authors : new String[0];
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder iconPath(String iconPath) {
            this.iconPath = iconPath;
            return this;
        }

        public Builder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Resource build() {
            return new Resource(name, fileName, type, version, description, authors, enabled, iconPath, fileSize);
        }
    }
}
