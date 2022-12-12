import axios from "axios";

export const BGG_SNAPSHOT_URL = 'https://4ovtpf6sj9.execute-api.us-east-1.amazonaws.com';

export const api = axios.create({
    headers: {
        'Content-Type': 'application/json'
    }
});
