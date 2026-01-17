package de.gnm.voxeldash.api.ssh;

import de.gnm.voxeldash.api.controller.SSHController;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.sftp.server.Handle;
import org.apache.sshd.sftp.server.SftpEventListener;

import java.io.IOException;

public class SFTPListener implements SftpEventListener {

    private final SSHController sshController;

    public SFTPListener(SSHController sshController) {
        this.sshController = sshController;
    }

    @Override
    public void opening(ServerSession session, String remoteHandle, Handle localHandle) throws IOException {
        if (!sshController.isSFTPEnabled()) {
            throw new IOException("SFTP is disabled in this server. If you think this is an error, please contact the server administrator.");
        }
        session.setAttribute(sshController.getIsSFTP(), true);
    }
}
