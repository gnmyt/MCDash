package de.gnmyt.mcdash.connector;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.ConfigurationManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MasterConnector {

    private final String REQUEST_FORMAT = "%s/api/register";

    private final OkHttpClient client = new OkHttpClient().newBuilder().build();
    private final ConfigurationManager config = MinecraftDashboard.getDashboardConfig();

    private int attempted_trys = 0;

    /**
     * Gets the local ip address of the computer
     * @return the local ip address of the computer
     */
    public String getLocalAddress() {
        try {
            return config.hasString("customIP") ? config.getString("customIP") : InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) { }
        return "localhost";
    }

    /**
     * Prepares the registration request
     * @return the prepared registration request
     */
    public Request prepareRequest() {
        return new Request.Builder()
                .url(String.format(REQUEST_FORMAT, config.getMasterIP()))
                .post(new FormBody.Builder()
                        .addEncoded("identifier", String.valueOf(config.getIdentifier()))
                        .addEncoded("wrapperIP", getLocalAddress()+":"+config.getWrapperPort())
                        .addEncoded("wrapperKey", config.getWrapperKey())
                        .build())
                .header("Authorization", "Bearer " + config.getMasterKey())
                .build();
    }

    /**
     * Updates the wrapper data in the master backend
     */
    public void register() {
        attempted_trys++;

        if (attempted_trys > 5) return;

        try {
            Response response = client.newCall(prepareRequest()).execute();
            response.body().close();
            if (response.code() > 200) {
                throw new Exception("Request not successful");
            }
            System.out.println(MinecraftDashboard.getPrefix() + "Successfully registered the server");
        } catch (Exception e) {

            if (attempted_trys == 5) {
                MinecraftDashboard.disablePlugin("Could not connect to master, please check the config.yml file");
            } else System.out.println(String.format("%sRegistration failed, retrying in 5 seconds... (try %d/5)", MinecraftDashboard.getPrefix(), attempted_trys));

            try { Thread.sleep(5000); } catch (InterruptedException ignored) { }

            register();
        }
    }

}
