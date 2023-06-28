package de.gnmyt.mcdash.panel.routes.backups;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.controller.BackupController;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class BackupRestoreRoute extends DefaultHandler {

    private final BackupController controller = MinecraftDashboard.getBackupController();

    @Override
    public String path() {
        return "restore";
    }

    /**
     * Restores a backup
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can occur while executing the code
     */
    @Override
    public void post(Request request, ResponseController response) throws Exception {
        if (!isStringInBody("backup_id")) return;
        if (!isBooleanInBody("halt")) return;

        String backupId = getStringFromBody("backup_id");
        boolean restart = getBooleanFromBody("halt");

        if (!controller.backupExists(backupId)) {
            response.code(404).message("Backup not found");
            return;
        }

        controller.restoreBackup(backupId, restart);
        response.message("Backup restored");
    }
}
