import { io } from "socket.io-client";

export const socket = io("https://tools-api.gnmyt.dev", {autoConnect: false});