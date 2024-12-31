package de.gnm.mcdash.api.controller;

import org.apache.sshd.common.config.keys.loader.openssh.kdf.BCrypt;

import java.sql.Connection;

public class AccountController extends BaseController {

    public AccountController(Connection connection) {
        super(connection);

        createTable();
    }

    /**
     * Helper method to create the accounts table
     */
    private void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
    }

    /**
     * Create an account with a username and password
     *
     * @param username The username of the account
     * @param password The password of the account
     * @return whether the account was created successfully
     */
    public boolean createAccount(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return executeUpdate("INSERT INTO accounts (username, password) VALUES (?, ?)", username, hashedPassword) > 0;
    }

    /**
     * Get the user id of a user by the username
     *
     * @param username The username of the user
     * @return the user id of the user
     */
    public int getUserId(String username) {
        return (int) getSingleResult("SELECT id FROM accounts WHERE username = ?", username).get("id");
    }

    /**
     * Delete an account by the username
     *
     * @param username The username of the account
     * @return whether the account was deleted successfully
     */
    public boolean deleteAccount(String username) {
        return executeUpdate("DELETE FROM accounts WHERE username = ?", username) > 0;
    }

    /**
     * Check if an account exists by the username
     *
     * @param username The username of the account
     * @return whether the account exists
     */
    public boolean accountExists(String username) {
        return getSingleResult("SELECT * FROM accounts WHERE username = ?", username) != null;
    }

    /**
     * Check if a password is valid for a username
     *
     * @param username The username of the account
     * @param password The password to check
     * @return whether the password is valid
     */
    public boolean isValidPassword(String username, String password) {
        if (!accountExists(username)) return false;

        String hashedPassword = (String) getSingleResult("SELECT password FROM accounts WHERE username = ?", username).get("password");
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * Change the password of an account by the username
     * @param username The username of the account
     * @param password The new password of the account
     * @return whether the password was changed successfully
     */
    public boolean changePassword(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return executeUpdate("UPDATE accounts SET password = ? WHERE username = ?", hashedPassword, username) > 0;
    }

    /**
     * Change the username of an account by the username
     * @param username The username of the account
     * @param newUsername The new username of the account
     * @return whether the username was changed successfully
     */
    public boolean changeUsername(String username, String newUsername) {
        return executeUpdate("UPDATE accounts SET username = ? WHERE username = ?", newUsername, username) > 0;
    }

}
