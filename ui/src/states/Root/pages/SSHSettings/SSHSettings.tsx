import {FormEvent, useContext, useEffect, useState} from "react";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Switch} from "@/components/ui/switch";
import {Label} from "@/components/ui/label";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Check, Globe, Lock, Power, Server, X} from "lucide-react";
import {jsonRequest, patchRequest, postRequest} from "@/lib/RequestUtil.ts";
import {SSHSession} from "@/types/ssh.ts";
import {ServerInfoContext} from "@/contexts/ServerInfoContext.tsx";
import {t} from "i18next";

export default function SSHSettings() {
    const [sshPort, setSshPort] = useState("");
    const [serverEnabled, setServerEnabled] = useState(true);
    const [sftpEnabled, setSftpEnabled] = useState(true);
    const [consoleEnabled, setConsoleEnabled] = useState(true);
    const [activeSessions, setActiveSessions] = useState<SSHSession[]>([]);

    const {serverInfo} = useContext(ServerInfoContext)!;

    const handleConnect = () => {
        window.open(`sftp://${serverInfo?.accountName}@${window.location.hostname}:${sshPort}`, "_blank");
    }

    const disconnectSession = (sessionId: string) =>
        postRequest("/service/ssh/disconnect", {sessionId}).then(() => fetchData());

    const fetchData = async () => {
        jsonRequest("service/ssh").then((data) => {
            setActiveSessions(data.activeClients || []);
            setServerEnabled(data.enabled);
            setSftpEnabled(data.sftpEnabled);
            setConsoleEnabled(data.consoleEnabled);
            setSshPort(data.port);
        });
    }

    const changeEnabled = (enabled: boolean) =>
        patchRequest("service/ssh/enabled", {value: enabled}).then(() => fetchData());

    const changePort = (e: FormEvent) => {
        e.preventDefault();
        patchRequest("service/ssh/port", {value: sshPort}).then(() => fetchData());
    }

    const changeSftp = (enabled: boolean) =>
        patchRequest("service/ssh/sftpEnabled", {value: enabled}).then(() => fetchData());

    const changeConsole = (enabled: boolean) =>
        patchRequest("service/ssh/consoleEnabled", {value: enabled}).then(() => fetchData());

    useEffect(() => {
        fetchData();
    }, []);

    return (

        <main className="flex-1 overflow-y-auto p-8">
            <div className="grid gap-8 md:grid-cols-2">
                <Card className="col-span-full">
                    <CardHeader className="flex justify-between">
                        <div>
                            <CardTitle className="flex items-center gap-2">
                                <Power className="h-6 w-6"/>
                                {t("ssh.status.title")}
                            </CardTitle>
                            <CardDescription>{t("ssh.status.description")}</CardDescription>
                        </div>
                    </CardHeader>
                    <CardContent className="flex items-center justify-between">
                        <div className="flex items-center space-x-2">
                            <Switch id="server-status" checked={serverEnabled} onCheckedChange={changeEnabled}/>
                            <Label htmlFor="server-status">
                                {serverEnabled ? t("ssh.status.enabled") : t("ssh.status.disabled")}
                            </Label>
                        </div>
                        <Button onClick={handleConnect} disabled={!serverEnabled} className="flex items-center gap-2">
                            {t("ssh.status.connect")}
                        </Button>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <Globe className="h-6 w-6"/>
                            {t("ssh.port.title")}
                        </CardTitle>
                        <CardDescription>{t("ssh.port.description")}</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <form className="flex items-center space-x-2" onSubmit={changePort}>
                            <Input onChange={(e) => setSshPort(e.target.value)}
                                   min="1" value={sshPort || ""} max="65535" className="w-24"
                                   disabled={!serverEnabled}/>
                            <Button type="submit" disabled={!serverEnabled}>{t("ssh.port.change")}</Button>
                        </form>

                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <Lock className="h-6 w-6"/>
                            {t("ssh.access.title")}
                        </CardTitle>
                        <CardDescription>{t("ssh.access.description")}</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="flex items-center space-x-2">
                            <Switch id="sftp-access" checked={sftpEnabled} onCheckedChange={changeSftp}
                                    disabled={!serverEnabled}/>
                            <Label htmlFor="sftp-access">
                                {sftpEnabled ? t("ssh.access.sftp_enabled") : t("ssh.access.sftp_disabled")}
                            </Label>
                        </div>
                        <div className="flex items-center space-x-2">
                            <Switch id="console-access" checked={consoleEnabled} onCheckedChange={changeConsole}
                                    disabled={!serverEnabled}
                            />
                            <Label htmlFor="console-access">
                                {consoleEnabled ? t("ssh.access.console_enabled") : t("ssh.access.console_disabled")}
                            </Label>
                        </div>
                    </CardContent>
                </Card>

                <Card className="col-span-full">
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <Server className="h-6 w-6"/>
                            {t("ssh.sessions.title")}
                        </CardTitle>
                        <CardDescription>{t("ssh.sessions.description")}</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead className="w-[70%]">{t("ssh.sessions.username")}</TableHead>
                                    <TableHead className="w-[16%]">{t("ssh.sessions.ip")}</TableHead>
                                    <TableHead className="w-[10%]">{t("ssh.sessions.sftp")}</TableHead>
                                    <TableHead className="w-[4%] text-right">{t("ssh.sessions.actions")}</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {activeSessions.map((session: SSHSession) => (
                                    <TableRow key={session.username}>
                                        <TableCell className="flex items-center space-x-2">
                                            <Avatar className="h-6 w-6">
                                                <AvatarImage src={`https://minotar.net/avatar/${session.username}.png`}
                                                             alt={session.username}/>
                                                <AvatarFallback>{session.username.charAt(0).toUpperCase()}</AvatarFallback>
                                            </Avatar>
                                            <span>{session.username}</span>
                                        </TableCell>
                                        <TableCell>{session.address}</TableCell>
                                        <TableCell>{session.isSFTP ? <Check /> : <X />}</TableCell>
                                        <TableCell className="flex items-center justify-end">
                                            <Button onClick={() => disconnectSession(session.sessionId)}>
                                                {t("ssh.sessions.disconnect")}
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </CardContent>
                </Card>
            </div>
        </main>
    );
}
