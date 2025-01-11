package de.gnm.mcdash.api.ssh;

import de.gnm.mcdash.api.controller.SSHController;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

import java.io.IOException;

public class SSHShellFactory implements ShellFactory {

    private final SSHController sshController;

    public SSHShellFactory(SSHController sshController) {
        this.sshController = sshController;
    }

    @Override
    public Command createShell(ChannelSession channelSession) throws IOException {
        return sshController.isConsoleEnabled() ? new SSHShell() : new SSHShellDisabled();
    }
}
