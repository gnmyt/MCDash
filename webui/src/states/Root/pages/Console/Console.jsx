import {dispatchCommand, request} from "@/common/utils/RequestUtil";
import React, {useEffect, useRef, useState} from "react";
import {Terminal} from "xterm";
import {FitAddon} from "xterm-addon-fit";

import "xterm/css/xterm.css";
import "./custom.css";
import {Box, IconButton, Stack, TextField, Typography} from "@mui/material";
import {Send} from "@mui/icons-material";

export const Console = () => {
    const terminalRef = useRef(null);
    const [command, setCommand] = useState("");

    useEffect(() => {
        const terminal = new Terminal({fontSize: 14});

        let currentLine = 1;

        const fitAddon = new FitAddon();
        terminal.loadAddon(fitAddon);

        const resize = () => fitAddon.fit();

        window.addEventListener("resize", resize);

        terminal.open(terminalRef.current);
        fitAddon.fit();

        const updateConsole = () => {
            request("console/?startLine=" + currentLine).then(async (r) => {
                const lines = (await r.text()).split("\n");
                let lineAmount = lines.length;
                if (lines.length === 1 && lines[0] === "") return;

                if (currentLine === 0 && lines.length >= 100) lines.splice(0, lines.length - 100);

                lines.forEach((line) => terminal.writeln(line));

                currentLine += lineAmount;
            });
        };

        const interval = setInterval(() => {
            updateConsole();
        }, 2000);

        updateConsole();

        return () => {
            terminal.dispose();
            window.removeEventListener("resize", resize);
            clearInterval(interval);
        };
    }, []);

    return (
        <>
            <Typography variant="h5" fontWeight={500}>Console</Typography>

            <Box ref={terminalRef} sx={{mt: 2, width: "85vw", borderRadius: 1.5, overflow: "hidden"}}/>

            <Stack component="form" direction="row" alignItems="center" gap={1} sx={{mt: 3}} onSubmit={(e) => {
                e.preventDefault();
                dispatchCommand(command).then(() => setCommand(""));
            }}>
                <TextField value={command} required fullWidth label="Command"
                           autoFocus onChange={(e) => setCommand(e.target.value)}/>
                <IconButton variant="contained" type="submit">
                    <Send/>
                </IconButton>
            </Stack>
        </>
    );
};