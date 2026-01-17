package de.gnm.voxeldash.api.helper;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchiveHelper {

    private static final int BUFFER_SIZE = 8192;

    public static InputStream createZipStream(File folder) throws IOException {
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("Folder does not exist or is not a directory");
        }

        File tempFile = File.createTempFile("voxeldash_folder_", ".zip");
        tempFile.deleteOnExit();

        try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(tempFile.toPath())))) {
            zipOut.setLevel(6);
            zipDirectory(folder, folder.getName(), zipOut);
        }

        return new FilterInputStream(new BufferedInputStream(Files.newInputStream(tempFile.toPath()))) {
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    tempFile.delete();
                }
            }
        };
    }

    private static void zipDirectory(File directory, String parentPath, ZipOutputStream zipOut) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            String entryPath = parentPath.isEmpty() ? file.getName() : parentPath + "/" + file.getName();
            if (file.isDirectory()) {
                zipOut.putNextEntry(new ZipEntry(entryPath + "/"));
                zipOut.closeEntry();
                zipDirectory(file, entryPath, zipOut);
            } else {
                zipFile(file, entryPath, zipOut);
            }
        }
    }

    private static void zipFile(File file, String entryName, ZipOutputStream zipOut) throws IOException {
        zipOut.putNextEntry(new ZipEntry(entryName));
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                zipOut.write(buffer, 0, bytesRead);
            }
        }
        zipOut.closeEntry();
    }
}
