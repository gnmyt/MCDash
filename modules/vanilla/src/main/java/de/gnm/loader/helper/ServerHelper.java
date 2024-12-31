package de.gnm.loader.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.event.EventDispatcher;
import de.gnm.mcdash.api.event.console.ConsoleMessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerHelper {

    private static final Logger LOG = Logger.getLogger("MCDashVanilla");
    private static final String SERVER_JAR = "server.jar";
    private static final String AIKARS_FLAGS = "-XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+ParallelRefProcEnabled " +
            "-XX:+PerfDisableSharedMem -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1HeapRegionSize=8M " +
            "-XX:G1HeapWastePercent=5 -XX:G1MaxNewSizePercent=40 -XX:G1MixedGCCountTarget=4 " +
            "-XX:G1MixedGCLiveThresholdPercent=90 -XX:G1NewSizePercent=30 -XX:G1RSetUpdatingPauseTimePercent=5 " +
            "-XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=15 -XX:MaxGCPauseMillis=200 " +
            "-XX:MaxTenuringThreshold=1 -XX:SurvivorRatio=32 -Dusing.aikars.flags=https://mcflags.emc.gs " +
            "-Daikars.new.flags=true";

    private final File serverRoot;
    private final EventDispatcher eventDispatcher;
    private final ManifestHelper manifestHelper;
    private final OkHttpClient client;
    private Process process;

    public ServerHelper(File serverRoot, EventDispatcher eventDispatcher) {
        this.serverRoot = serverRoot;
        this.manifestHelper = new ManifestHelper();
        this.client = new OkHttpClient();
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Checks if the server.jar is installed
     * @return {@code true} if the server.jar is installed
     */
    public boolean isInstalled() {
        return new File(serverRoot, SERVER_JAR).exists();
    }

    /**
     * Installs the latest server version
     */
    public void install() {
        try {
            JsonNode latestRelease = manifestHelper.getLatestRelease();
            if (latestRelease == null) {
                LOG.error("Failed to get latest release information");
                return;
            }

            ObjectNode versionManifest = manifestHelper.getVersionManifest(latestRelease.get("url").asText());
            if (versionManifest == null) {
                LOG.error("Failed to get version manifest");
                return;
            }

            LOG.info("Installing server version: " + versionManifest.get("id").asText());

            String serverUrl = versionManifest.get("downloads").get("server").get("url").asText();
            downloadServer(serverUrl);
        } catch (Exception e) {
            LOG.error("Failed to install server", e);
        }
    }

    /**
     * Downloads the server from the given server url
     * @param serverUrl The server url
     * @throws IOException if the download failed
     */
    private void downloadServer(String serverUrl) throws IOException {
        Request request = new Request.Builder().url(serverUrl).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Failed to download server: " + response);
            }

            File serverFile = new File(serverRoot, SERVER_JAR);
            try (InputStream inputStream = response.body().byteStream()) {
                Files.copy(inputStream, serverFile.toPath());
            }

            setServerFilePermissions(serverFile);
        }
    }

    /**
     * Sets the permissions for the server file
     * @param serverFile The server file
     */
    private void setServerFilePermissions(File serverFile) {
        serverFile.setWritable(true);
        serverFile.setReadable(true);
        serverFile.setExecutable(true);
    }

    /**
     * Creates the server process
     */
    public void createProcess() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(buildCommandList())
                    .directory(serverRoot)
                    .redirectErrorStream(true);

            process = processBuilder.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        eventDispatcher.dispatch(new ConsoleMessageReceivedEvent(line));
                    }
                } catch (IOException e) {
                    LOG.error("Failed to read server output", e);
                }
            }).start();

            setupProcessExitHandler();
        } catch (Exception e) {
            LOG.error("Failed to create server process", e);
        }
    }

    /**
     * Builds the command list for the server process
     * @return the command list
     */
    private List<String> buildCommandList() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long xmx = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);
        String javaPath = getJavaPath();

        List<String> flagsList = new ArrayList<>();
        flagsList.add(javaPath);
        flagsList.addAll(Arrays.asList(AIKARS_FLAGS.split(" ")));
        flagsList.addAll(Arrays.asList(
                "-Xmx" + xmx + "M",
                "-Xms" + (xmx / 2) + "M",
                "-jar",
                SERVER_JAR,
                "-nogui"
        ));

        LOG.info("Starting server with: " + String.join(" ", flagsList));
        return flagsList;
    }

    /**
     * Gets the java path
     * @return the java path
     */
    private String getJavaPath() {
        String javaHome = System.getProperty("java.home");
        String binaryPath = "/bin/java" + (System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : "");
        return javaHome + binaryPath;
    }

    /**
     * Gets the output stream of the server process
     * @return the output stream of the server process
     */
    public OutputStream getOutputStream() {
        if (process == null) {
            return null;
        }

        return process.getOutputStream();
    }

    /**
     * Sets up the process exit handler
     */
    private void setupProcessExitHandler() {
        if (process != null) {
            process.onExit().thenAccept(p -> {
                LOG.info("Server stopped with exit code: " + p.exitValue());
                System.exit(p.exitValue());
            });
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        }
    }

    /**
     * Starts the server
     */
    public void startup() {
        if (!isInstalled()) {
            install();
        }
        createProcess();
    }

    /**
     * Shuts down the server
     */
    public void shutdown() {
        if (process != null && process.isAlive()) {
            process.destroy();
            LOG.info("Server shutdown initiated");
        }
    }
}