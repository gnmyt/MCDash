import {dispatchCommand, request} from "@/common/utils/RequestUtil";
import React, {useEffect, useRef, useState} from "react";
import {Terminal} from "xterm";
import {FitAddon} from "xterm-addon-fit";

import "xterm/css/xterm.css";
import "./custom.css";
import {Box, IconButton, Stack, TextField, Typography} from "@mui/material";
import {Send} from "@mui/icons-material";
import {t} from "i18next";

export const Console = () => {

    const [consoleHistory, setConsoleHistory] = useState(JSON.parse(localStorage.getItem("consoleHistory")) || []);
    const [currentHistoryIndex, setCurrentHistoryIndex] = useState(consoleHistory.length);

    const pushHistory = (command) => {
        if (consoleHistory.length >= 25) consoleHistory.splice(0, 1);
        consoleHistory.push(command);
        setConsoleHistory(consoleHistory);
        localStorage.setItem("consoleHistory", JSON.stringify(consoleHistory));
        setCurrentHistoryIndex(consoleHistory.length);

        setCommand("");
    }

    const onHistoryKeyUp = (e) => {
        if (e.key === "ArrowUp") {
            if (currentHistoryIndex > 0) {
                setCurrentHistoryIndex(currentHistoryIndex - 1);
                setCommand(consoleHistory[currentHistoryIndex - 1]);
            }
        } else if (e.key === "ArrowDown") {
            setCurrentHistoryIndex(currentHistoryIndex < consoleHistory.length - 1 ? currentHistoryIndex + 1 : consoleHistory.length);
            setCommand(currentHistoryIndex < consoleHistory.length - 1 ? consoleHistory[currentHistoryIndex + 1] : "");
        }
    }

    const executeCommand = (event) => {
        event.preventDefault();
        dispatchCommand(command).then(() => pushHistory(command));
    }

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

                lines.forEach((line) => {
                    const logLevelRegex = /\[(\d{2}:\d{2}:\d{2})] \[.*?\/(INFO|WARN(ING)?|ERROR)]: /;

                    line = line.replace(logLevelRegex, (match, time, level) => {
                        let colorCode = '\x1b[0m';
                        if (level === 'INFO') colorCode = '\x1b[34m';
                        else if (level === 'WARN' || level === 'WARNING') colorCode = '\x1b[33m';
                        else if (level === 'ERROR') colorCode = '\x1b[31m';

                        return `[${time}] [${colorCode}${level}\x1b[0m]: ${colorCode === '\x1b[34m' ? '' : colorCode}`;
                    });

                    terminal.writeln(line + '\x1b[0m');
                });

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
            <Typography variant="h5" fontWeight={500}>{t("nav.console")}</Typography>

            <Box ref={terminalRef} sx={{mt: 2, width: "85vw", borderRadius: 1.5, overflow: "hidden"}}/>

            <Stack component="form" direction="row" alignItems="center" gap={1} sx={{mt: 3}} onSubmit={executeCommand}>
                <TextField value={command} required fullWidth label={t("console.command")}
                           autoFocus onChange={(e) => setCommand(e.target.value)} onKeyUp={onHistoryKeyUp}/>

                <IconButton variant="contained" type="submit"><Send/></IconButton>
            </Stack>
        </>
    );
};