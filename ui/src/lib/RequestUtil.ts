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
export const deleteRequest = async (path: string, headers = {}) => {
    return (await request(path, "DELETE", undefined, headers)).json();
}

// Run a PATCH request and get the json of the response
export const patchRequest = async (path: string, body = {}, headers = {}) => {
    return (await request(path, "PATCH", body, headers)).json();
}