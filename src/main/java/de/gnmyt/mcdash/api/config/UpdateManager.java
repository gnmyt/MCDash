package de.gnmyt.mcdash.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnmyt.mcdash.MinecraftDashboard;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateManager {
    private static final String ROOT_URL = "https://api.spiget.org/v2/";
    private static final int RESOURCE_ID = 110687;

    private final ScheduledExecutorService scheduler;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final MinecraftDashboard instance;

    private final String currentVersion;
    private String latestVersion;

    /**
     * Constructor of the {@link UpdateManager}
     * @param instance The instance of the {@link MinecraftDashboard}
     */
    public UpdateManager(MinecraftDashboard instance) {
        this.instance = instance;
        this.currentVersion = instance.getDescription().getVersion();
        this.scheduler = Executors.newScheduledThreadPool(1);

        checkLatestVersion();

        scheduler.scheduleAtFixedRate(this::checkLatestVersion, 0, 6, TimeUnit.HOURS);
    }

    /**
     * Shuts down the scheduler
     */
    public void shutdownScheduler() {
        scheduler.shutdownNow();
    }

    /**
     * Checks the latest version of the plugin
     */
    private void checkLatestVersion() {
        try {
            HttpUrl url = HttpUrl.parse(ROOT_URL + "resources/" + RESOURCE_ID + "/versions/latest").newBuilder().build();
            Response httpResponse = client.newCall(new okhttp3.Request.Builder().url(url).build()).execute();

            if (httpResponse.code() != 200) {
                latestVersion = currentVersion;
                return;
            }

            latestVersion = mapper.readTree(httpResponse.body().string()).get("name").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the plugin to the latest version from spigot
     */
    public void update(boolean reload) {
        try {
            String fileUrl = ROOT_URL + "resources/" + RESOURCE_ID + "/download";

            String currentFile = instance.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            String currentFileName = currentFile.substring(currentFile.lastIndexOf("/") + 1);

            FileUtils.copyInputStreamToFile(client.newCall(new okhttp3.Request.Builder().url(fileUrl).build())
                    .execute().body().byteStream(), new File("./plugins/MCDash-" + latestVersion + ".jar"));

            Bukkit.getScheduler().runTaskLater(instance, () -> {
                Bukkit.getPluginManager().disablePlugin(instance);
                try {
                    new File("./plugins/" + currentFileName).delete();
                } catch (Exception ignored) {}

                if (reload) Bukkit.getServer().reload();
            }, 20L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current version of the plugin
     * @return the current version of the plugin
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Gets the latest version of the plugin
     * @return the latest version of the plugin
     */
    public String getLatestVersion() {
        return latestVersion;
    }

    /**
     * Checks if the current version is the latest version
     * @return <code>true</code> if the current version is the latest version, otherwise <code>false</code>
     */
    public boolean isLatestVersion() {
        return currentVersion.equals(latestVersion);
    }
}
