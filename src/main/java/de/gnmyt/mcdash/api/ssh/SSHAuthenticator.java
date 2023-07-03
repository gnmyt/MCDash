package de.gnmyt.mcdash.api.ssh;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.AccountManager;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.session.ServerSession;

public class SSHAuthenticator implements PasswordAuthenticator {

    private final AccountManager accountManager = MinecraftDashboard.getAccountManager();

    /**
     * Checks if the given username and password are valid
     * @param username The username of the user
     * @param password The password of the user
     * @param serverSession The current {@link ServerSession}
     * @return <code>true</code> if the username and password are valid, otherwise <code>false</code>
     * @throws PasswordChangeRequiredException Will be thrown if the password needs to be changed
     * @throws AsyncAuthException Will be thrown if the authentication is not finished yet
     */
    @Override
    public boolean authenticate(String username, String password, ServerSession serverSession) throws PasswordChangeRequiredException, AsyncAuthException {
        return accountManager.isValidPassword(username, password);
    }

}
