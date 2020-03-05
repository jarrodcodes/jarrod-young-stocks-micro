let client;

export const initws = () => {
    client = new WebSocket('ws://localhost:9000/ws');
    return client
};

export const getws = () => {
    return client
};