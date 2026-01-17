package de.gnm.mcdash.api.ssh;

import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SSHShellDisabled implements Command {

    private ExitCallback exitCallback;
    private OutputStream out;
    private OutputStream err;
    private InputStream in;

    @Override
    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;
    }

    @Override
    public void setErrorStream(OutputStream err) {
        this.err = err;
    }

    @Override
    public void setInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public void setOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        String message = "SSH is disabled in this server. If you think this is an error, please contact the server administrator.\n";

        out.write(message.getBytes());
        out.flush();

        if (exitCallback != null) {
            exitCallback.onExit(0);
        }
    }

    @Override
    public void destroy(ChannelSession channelSession) throws Exception {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (err != null) {
            err.close();
        }
    }

}
