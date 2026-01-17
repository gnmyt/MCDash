package de.gnm.loader.pipes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.gnm.voxeldash.api.entities.OfflinePlayer;
import de.gnm.voxeldash.api.pipes.players.WhitelistPipe;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Properties;

public class WhitelistPipeImpl implements WhitelistPipe {

    private static final Logger LOG = LogManager.getLogger("VoxelDashVanilla");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String WHITELIST_JSON_PATH = "whitelist.json";

    private final BufferedWriter consoleWriter;

    public WhitelistPipeImpl(OutputStream console) {
        this.consoleWriter = new BufferedWriter(new OutputStreamWriter(console));
    }

    @Override
    public void setStatus(boolean status) {
        try {
            consoleWriter.write("whitelist " + (status ? "on" : "off") + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to set whitelist status to " + status, e);
        }
    }

    @Override
    public boolean getStatus() {
        try {
            Properties properties = new Properties();
            properties.load(new File("server.properties").toURI().toURL().openStream());
            return properties.getProperty("white-list", "false").equals("true");
        } catch (Exception e) {
            LOG.error("Failed to read whitelist status from server.properties", e);
        }
        return false;
    }

    @Override
    public ArrayList<OfflinePlayer> getWhitelistedPlayers() {
        ArrayList<OfflinePlayer> whitelist = new ArrayList<>();
        try {
            File whitelistFile = new File(WHITELIST_JSON_PATH);
            if (!whitelistFile.exists()) {
                LOG.warn("whitelist.json file not found at " + WHITELIST_JSON_PATH);
                return whitelist;
            }

            ArrayNode whitelistArray = (ArrayNode) MAPPER.readTree(whitelistFile);
            for (JsonNode player : whitelistArray) {
                whitelist.add(new OfflinePlayer(player.get("name").asText(), player.get("uuid").asText()));
            }

            return whitelist;

        } catch (Exception e) {
            LOG.error("Failed to read whitelist from whitelist.json", e);
        }
        return whitelist;
    }

    @Override
    public void addPlayer(String playerName) {
        try {
            consoleWriter.write("whitelist add " + playerName + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to add " + playerName + " to the whitelist", e);
        }
    }

    @Override
    public void removePlayer(String playerName) {
        try {
            consoleWriter.write("whitelist remove " + playerName + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to remove " + playerName + " from the whitelist", e);
        }
    }
}
