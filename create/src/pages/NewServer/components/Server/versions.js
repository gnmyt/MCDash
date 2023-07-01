const versions = [
    "1.20.1", "1.19.4", "1.19.3", "1.19.2", "1.19.1",
    "1.19", "1.18.2", "1.18.1", "1.18", "1.17.1", "1.17", "1.16.5",
    "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.15.2",
    "1.15.1", "1.15", "1.14.4", "1.14.3", "1.14.2", "1.14.1",
    "1.14", "1.13.2", "1.13.1", "1.13", "1.12.2", "1.12.1",
    "1.12", "1.11.2", "1.11.1", "1.11", "1.10.2", "1.10",
    "1.9.4", "1.9.2", "1.9", "1.8.8"
]

export const getVersions = (server) => {
    if (server === "purpur") {
        return versions.filter(version => {
            if ( version === "1.14") return false;
            const versionNumber = version.split(".")[1];
            return versionNumber >= 14;
        });
    }

    if (server === "paper") {
        return versions.filter(version => {
            if (!["1.11.1", "1.11", "1.10", "1.9.2", "1.9"].includes(version)) return true;
        });
    }

    return versions;
}

export const getJavaVersion = (version) => {
    const versionNumber = version.split(".")[1];
    if (versionNumber >= 17) return 17;
    if (versionNumber >= 16) return 16;
    if (versionNumber >= 12) return 11;
    return 8;
}