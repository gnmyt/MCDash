import React, {useState, useRef, useEffect, useContext} from 'react';
import {Input} from "@/components/ui/input";
import {ScrollArea} from "@/components/ui/scroll-area";
import {SocketContext} from "@/contexts/SocketContext";
import {postRequest} from "@/lib/RequestUtil";

interface LogEntry {
    text: string;
    color: string;
}

interface SocketMessage {
    data: string;
    event: string;
    message: string;
}

const Console = () => {
    const {lastMessage, attachEventListener, detachEventListener} = useContext(SocketContext)!;
    const [log, setLog] = useState<LogEntry[]>([]);
    const [input, setInput] = useState<string>('');
    const bottomRef = useRef<HTMLDivElement>(null);

    const [history, setHistory] = useState<string[]>(() => {
        const saved = localStorage.getItem("consoleHistory");
        return saved ? JSON.parse(saved) : [];
    });

    const [historyIndex, setHistoryIndex] = useState<number>(-1);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({behavior: 'smooth'});
    }, [log]);

    useEffect(() => {
        attachEventListener('CONSOLE');
        return () => detachEventListener('CONSOLE');
    }, []);

    useEffect(() => {
        if (lastMessage) {
            try {
                const data = JSON.parse(lastMessage.data) as SocketMessage;
                if (data.event === 'CONSOLE') {
                    const message = data.message;
                    setLog((prev) => [...prev, {text: message, color: getLogColor(message)}]);
                }
            } catch (e) {
                console.error(e);
            }
        }
    }, [lastMessage]);

    const getLogColor = (message: string): string => {
        if (message.includes('[ERROR]')) return 'text-red-500';
        if (message.includes('[WARN') || message.includes('[WARNING]')) return 'text-yellow-500';
        if (message.includes('[INFO]')) return 'text-blue-500';
        return 'text-green-500';
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (input.trim()) {
            const newHistory = [...history, input];
            if (newHistory.length > 25) newHistory.shift();
            setHistory(newHistory);
            localStorage.setItem("consoleHistory", JSON.stringify(newHistory));

            setInput("");
            setHistoryIndex(-1);
            postRequest("action/command", {command: input});
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (history.length === 0) return;

        if (e.key === "ArrowUp") {
            e.preventDefault();
            const newIndex = historyIndex < history.length - 1 ? historyIndex + 1 : historyIndex;
            setHistoryIndex(newIndex);
            setInput(history[history.length - 1 - newIndex] || '');
        } else if (e.key === "ArrowDown") {
            e.preventDefault();
            const newIndex = historyIndex > 0 ? historyIndex - 1 : -1;
            setHistoryIndex(newIndex);
            setInput(newIndex === -1 ? '' : history[history.length - 1 - newIndex]);
        }
    };

    return (
        <div className="container mx-auto px-4">
            <div className="relative w-full overflow-hidden">
                <div className="bg-black text-green-500 font-mono p-4 mb-8 rounded-md">
                    <ScrollArea className="h-[400px] mb-4">
                        <div className="pr-4">
                            {log.map((entry, index) => (
                                <div key={index} className={`${entry.color} break-all`}>
                                    {entry.text}
                                </div>
                            ))}
                            <div ref={bottomRef}/>
                        </div>
                    </ScrollArea>
                    <form onSubmit={handleSubmit} className="flex items-center">
                        <span className="mr-2">{'>'}</span>
                        <Input
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={handleKeyDown}
                            className="flex-grow bg-transparent border-none text-green-500 focus:outline-none"
                            autoFocus
                        />
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Console;