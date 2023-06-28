package de.gnmyt.mcdash.panel.routes.backups;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.controller.BackupController;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.apache.commons.io.FileUtils;

public class BackupDownloadRoute extends DefaultHandler {

    private final BackupController controller = MinecraftDashboard.getBackupController();

    @Override
    public String path() {
        return "download";
    }

    /**
     * Downloads a backup
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can occur while executing the code
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        if (!isStringInQuery(request, response, "backup_id")) return;

        String backupId = getStringFromQuery(request, "backup_id");

        if (!controller.backupExists(backupId)) {
            response.code(404).message("Backup not found");
            return;
        }

        response.header("Content-Disposition", "attachment; filename=Backup.zip");

        response.bytes(FileUtils.readFileToByteArray(controller.getBackup(backupId)));
    }
}
