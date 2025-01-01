// Get the default headers of the request
const getHeaders: () => HeadersInit = () => {
    let headers = {};

    if (localStorage.getItem("sessionToken")) {
        headers = {Authorization: "Bearer " + localStorage.getItem("sessionToken")};
    }

    return headers;
}

// Run a plain request with all default values
export const request = async (path: string, method = "GET", body = {}, headers = {}, abort = true) => {
    const controller = new AbortController();
    if (abort) setTimeout(() => {controller.abort()}, 10000);

    return await fetch("/api/" + path, {
        headers: {...getHeaders(), ...headers}, method,
        body: Object.keys(body).length ? JSON.stringify(body) : undefined,
        signal: controller.signal
    });
}

// Run a GET request and get the json of the response
export const jsonRequest = async (path: string, headers = {}) => {
    return (await request(path, "GET", undefined, headers)).json();
}

// Run a POST request and get the json of the response
export const postRequest = async (path: string, body = {}, headers = {}) => {
    return (await request(path, "POST", body, headers)).json();
}

// Run a PUT request and get the json of the response
export const putRequest = async (path: string, body = {}, headers = {}) => {
    return (await request(path, "PUT", body, headers)).json();
}

// Run a DELETE request and get the json of the response
export const deleteRequest = async (path: string, body = {}, headers = {}) => {
    return (await request(path, "DELETE", body, headers)).json();
}

// Run a PATCH request and get the json of the response
export const patchRequest = async (path: string, body = {}, headers = {}) => {
    return (await request(path, "PATCH", body, headers)).json();
}

// Run a GET request and download the file
export const downloadRequest = async (path: string, body = {}, headers = {}) => {
    const file = await request(path, "GET", body, headers);
    const element = document.createElement('a');
    const url = file.headers.get('Content-Disposition')?.split('filename=')[1] || "file";
    element.setAttribute("download", url.replaceAll("\"", ""));

    const blob = await file.blob();
    element.href = window.URL.createObjectURL(blob);
    document.body.appendChild(element);
    element.click();
    element.remove();
}


export const uploadChunks = async (
    file: File,
    directory: string,
    onProgress: (percent: number) => void
) => {
    const CHUNK_SIZE = 50 * 1024 * 1024;
    const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
    let uploadedChunks = 0;

    const handleBeforeUnload = (event: BeforeUnloadEvent) =>event.preventDefault();

    window.addEventListener("beforeunload", handleBeforeUnload);

    try {
        const initResponse = await request("files/upload/init", "POST", {});
        const initResult = await initResponse.json();
        const uuid = initResult.uuid;

        for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
            const chunkStart = chunkIndex * CHUNK_SIZE;
            const chunkEnd = Math.min(chunkStart + CHUNK_SIZE, file.size);
            const chunk = file.slice(chunkStart, chunkEnd);

            const uploadResponse = await fetch(`/api/files/upload/chunk/${uuid}/${chunkIndex}`, {
                method: "PUT",
                headers: getHeaders(),
                body: chunk,
            });

            if (!uploadResponse.ok) {
                throw new Error("Failed to upload chunk " + chunkIndex);
            }

            uploadedChunks++;
            onProgress(Math.round((uploadedChunks / totalChunks) * 100));
        }

        await request("files/upload/stop", "POST", {uuid, destinationPath: directory + file.name}, {}, false);
    } catch (error) {
        console.error("Upload failed:", error);
        throw error;
    } finally {
        window.removeEventListener("beforeunload", handleBeforeUnload);
        onProgress(0);
    }
};