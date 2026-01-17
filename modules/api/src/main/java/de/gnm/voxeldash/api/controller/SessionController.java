package de.gnm.voxeldash.api.controller;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.sql.Connection;

public class SessionController extends BaseController {

    public SessionController(Connection connection) {
        super(connection);

        createTable();
    }

    /**
     * Helper method to create the sessions table
     */
    private void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "token TEXT, " +
                "lastUsed DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "userAgent TEXT, " +
                "FOREIGN KEY (userId) REFERENCES accounts(id))");
    }

    /**
     * Generate a session token for a user
     *
     * @param userId    The user id of the user
     * @param userAgent The user agent of the user
     * @return the generated session token
     */
    public String generateSessionToken(int userId, String userAgent) {
        String token = RandomStringUtils.random(64, 0, 0, true, true, null, new SecureRandom());
        if (executeUpdate("INSERT INTO sessions (userId, token, userAgent) VALUES (?, ?, ?)", userId, token, userAgent) > 0) {
            return token;
        }

        return null;
    }

    /**
     * Delete all session tokens for a user
     *
     * @param userId The user id of the user
     * @return whether the session tokens were deleted successfully
     */
    public boolean deleteSessionTokens(int userId) {
        return executeUpdate("DELETE FROM sessions WHERE userId = ?", userId) > 0;
    }

    /**
     * Destroy a session by a token
     *
     * @param token The token of the session
     * @return whether the session was destroyed successfully
     */
    public boolean destroySession(String token) {
        return executeUpdate("DELETE FROM sessions WHERE token = ?", token) > 0;
    }

    /**
     * Check if a token is valid
     *
     * @param token The token to check
     * @return whether the token is valid
     */
    public boolean isValidToken(String token) {
        return getSingleResult("SELECT * FROM sessions WHERE token = ?", token) != null;
    }

    /**
     * Get the user id of a token
     *
     * @param token The token to get the user id of
     * @return the user id of the token
     */
    public int getUserIdByToken(String token) {
        if (!isValidToken(token)) return -1;

        return (int) getSingleResult("SELECT userId FROM sessions WHERE token = ?", token).get("userId");
    }

    /**
     * Updates the 'lastUsed' field of a session
     *
     * @param token The token of the session
     */
    public void updateLastUsed(String token) {
        executeUpdate("UPDATE sessions SET lastUsed = CURRENT_TIMESTAMP WHERE token = ?", token);
    }

    /**
     * Destroy all sessions for a user
     *
     * @param userId The user id of the user
     * @return whether the sessions were destroyed successfully
     */
    public boolean destroyAllSessionsForUser(int userId) {
        return executeUpdate("DELETE FROM sessions WHERE userId = ?", userId) > 0;
    }

}
