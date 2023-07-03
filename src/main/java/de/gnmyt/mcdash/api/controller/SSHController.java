package de.gnmyt.mcdash.api.controller;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.ssh.MCShellFactory;
import de.gnmyt.mcdash.api.ssh.SSHAuthenticator;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class SSHController {

    private SshServer sshServer;
    private final Path hostKey;

    /**
     * Basic constructor of the {@link SSHController}
     * @param api The current instance of the {@link MinecraftDashboard} api
     */
    public SSHController(MinecraftDashboard api) {
        hostKey = Paths.get("plugins//" + api.getName() + "//hostkey.ser");
    }

    /**
     * Starts the SSH server
     * @param port The port of the SSH server
     * @throws IOException Will be thrown if the SSH server could not be started
     */
    public void start(int port) throws IOException {
        if (sshServer != null) sshServer.stop();

        sshServer = SshServer.setUpDefaultServer();
        sshServer.setShellFactory(new MCShellFactory());
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostKey));

        sshServer.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get(".").toAbsolutePath().normalize()));
        sshServer.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory.Builder().build()));

        sshServer.setPasswordAuthenticator(new SSHAuthenticator());

        sshServer.setPort(port);
        sshServer.start();
    }

    /**
     * Stops the SSH server
     * @throws IOException Will be thrown if the SSH server could not be stopped
     */
    public void stop() throws IOException {
        if (sshServer != null) sshServer.stop();
        sshServer = null;
    }

}
