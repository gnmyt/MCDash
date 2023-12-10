package de.gnmyt.mcdash.api.controller;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.BackupManager;
import de.gnmyt.mcdash.panel.routes.filebrowser.FileRoute;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupController {

    private final BackupManager backupManager = MinecraftDashboard.getBackupManager();
    private final File backupFolder;

    /**
     * Basic constructor of the {@link BackupController}
     */
    public BackupController() {
        this.backupFolder = new File(backupManager.getBackupPath());

        if (!backupFolder.exists()) backupFolder.mkdirs();
    }

    /**
     * Checks if a backup exists
     * @param name The time code of the backup
     * @return <code>true</code> if the backup exists, otherwise <code>false</code>
     */
    public boolean backupExists(String name) {
        if (getBackup(name) == null) return false;
        return FileRoute.isValidFilePath(getBackup(name).getAbsolutePath());
    }

    /**
     * Zips a directory
     * @param directory The directory to zip
     * @param parent The parent of the directory
     * @param zipOutputStream The zip output stream
     */
    private void zipDirectory(File directory, String parent, ZipOutputStream zipOutputStream) {
        if (directory.getName().equals(backupFolder.getName())) return;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                zipDirectory(file, parent + "/" + file.getName(), zipOutputStream);
            } else {
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(parent + "/" + file.getName()));
                    zipOutputStream.write(IOUtils.toByteArray(Files.newInputStream(file.toPath())));
                    zipOutputStream.closeEntry();
                } catch (Exception ignored) { }
            }
        }
    }

    /**
     * Writes a file to a zip output stream
     * @param file The file to write
     * @param zipOutputStream The zip output stream
     */
    private void zipFile(File file, ZipOutputStream zipOutputStream) {
        try {
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            zipOutputStream.write(IOUtils.toByteArray(Files.newInputStream(file.toPath())));
            zipOutputStream.closeEntry();
        } catch (Exception ignored) { }
    }

    /**
     * Creates a new backup
     * @param modeSuffix The modes of the backup
     * @param paths The paths to back up
     * @throws IOException An exception that will be thrown if the backup could not be created
     */
    public void createBackup(String modeSuffix, File... paths) throws IOException {
        File backupFile = new File(backupFolder, (new Date().getTime()) + "-" + modeSuffix + ".zip");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(backupFile.toPath()))) {
            for (File file : paths) {
                if (file.exists()) {
                    if (file.isDirectory()) {
                        zipDirectory(file, file.getName(), zipOutputStream);
                    } else zipFile(file, zipOutputStream);
                }
            }
        }
    }

    /**
     * Deletes a backup
     * @param name The name of the backup
     * @throws IOException An exception that will be thrown if the backup could not be deleted
     */
    public void deleteBackup(String name) throws IOException {
        Files.deleteIfExists(getBackup(name).toPath());
    }

    /**
     * Clears all old folders (except the root folder)
     * @param stream The stream of the backup
     * @throws Exception An exception that will be thrown if the old folders could not be cleared
     */
    private void clearOldFolders(FileInputStream stream) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(stream);
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        while (zipEntry != null) {
            File file = new File(zipEntry.getName());

            if (file.getParentFile() != null && file.getParentFile().exists() && !file.getParentFile().equals(new File(".")))
                FileUtils.deleteDirectory(file.getParentFile());

            zipEntry = zipInputStream.getNextEntry();
        }
    }

    /**
     * Restores a backup
     * @param name The name of the backup
     * @param haltAfterRestore <code>true</code> if the server should be halted after the restore, otherwise <code>false</code>
     */
    public void restoreBackup(String name, boolean haltAfterRestore) {
        if (!backupExists(name)) return;
        byte[] buffer = new byte[1024];

        try {
            clearOldFolders(new FileInputStream(getBackup(name)));

            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(getBackup(name).toPath()));
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                File newFile = new File(zipEntry.getName());

                if (newFile.getParentFile() != null) newFile.getParentFile().mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) fileOutputStream.write(buffer, 0, len);
                fileOutputStream.close();

                zipEntry = zipInputStream.getNextEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (haltAfterRestore) Runtime.getRuntime().halt(0);
    }

    /**
     * Gets all backups
     * @return A list of all backups
     */
    public ArrayList<File> getBackups() {
        File[] backupFiles = FileUtils.listFiles(backupFolder, new String[]{"zip"}, true).toArray(new File[0]);
        Arrays.sort(backupFiles);

        return new ArrayList<>(Arrays.asList(backupFiles));
    }

    /**
     * Gets a backup
     * @param name The name of the backup
     * @return The backup
     */
    public File getBackup(String name) {
        FileFilter fileFilter = file -> file.getName().startsWith(name + "-") && file.getName().endsWith(".zip");
        File[] backupFiles = backupFolder.listFiles(fileFilter);
        if (backupFiles == null || backupFiles.length == 0) return null;
        return backupFiles[0];
    }

}
