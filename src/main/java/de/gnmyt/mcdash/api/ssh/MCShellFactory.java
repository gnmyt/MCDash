package de.gnmyt.mcdash.api.ssh;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

import java.io.IOException;

public class MCShellFactory implements ShellFactory {

    /**
     * Creates a new {@link MCCommand} instance
     * @param channelSession The current {@link ChannelSession}
     * @return the created {@link MCCommand} instance
     * @throws IOException Will be thrown if the command could not be created
     */
    @Override
    public Command createShell(ChannelSession channelSession) throws IOException {
        return new MCCommand();
    }

}
