export const prettyDownloadCount = (count) => {
    if (count < 1000) return count;
    if (count < 1000000) return (count / 1000).toFixed(1) + "K";
    return (count / 1000000).toFixed(1) + "M";
}