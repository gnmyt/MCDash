/**
 * Convert the size of the file to a human-readable format
 * @param size size of the file in bytes
 */
export const convertSize = (size: number) => {
    if (size < 1024) return size + " B";
    else if (size < 1024 * 1024) return (size / 1024).toFixed(2) + " KB";
    else if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + " MB";
    else return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
}