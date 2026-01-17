package de.gnm.loader.pipes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.gnm.voxeldash.api.entities.BannedPlayer;
import de.gnm.voxeldash.api.pipes.players.BanPipe;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class BanPipeImpl implements BanPipe {

    private static final Logger LOG = Logger.getLogger("VoxelDashVanilla");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BANNED_PLAYERS_JSON = "banned-players.json";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private final BufferedWriter consoleWriter;

    public BanPipeImpl(OutputStream console) {
        this.consoleWriter = new BufferedWriter(new OutputStreamWriter(console));
    }

    @Override
    public ArrayList<BannedPlayer> getBannedPlayers() {
        ArrayList<BannedPlayer> players = new ArrayList<>();

        try {
            File bannedFile = new File(BANNED_PLAYERS_JSON);
            if (!bannedFile.exists()) {
                LOG.debug("banned-players.json not found");
                return players;
            }

            ArrayNode bannedArray = (ArrayNode) MAPPER.readTree(bannedFile);
            for (JsonNode entry : bannedArray) {
                try {
                    String name = entry.get("name").asText();
                    String uuidStr = entry.get("uuid").asText();
                    UUID uuid = UUID.fromString(uuidStr);

                    String reason = entry.has("reason") ? entry.get("reason").asText() : null;
                    String source = entry.has("source") ? entry.get("source").asText() : "Unknown";

                    Date banDate = null;
                    if (entry.has("created")) {
                        try {
                            banDate = DATE_FORMAT.parse(entry.get("created").asText());
                        } catch (Exception ignored) {
                        }
                    }

                    Date expiry = null;
                    if (entry.has("expires") && !entry.get("expires").asText().equals("forever")) {
                        try {
                            expiry = DATE_FORMAT.parse(entry.get("expires").asText());
                        } catch (Exception ignored) {
                        }
                    }

                    BannedPlayer bannedPlayer = new BannedPlayer(name, uuid, reason, banDate, expiry, source);
                    players.add(bannedPlayer);

                } catch (Exception e) {
                    LOG.debug("Failed to parse banned player entry", e);
                }
            }

        } catch (Exception e) {
            LOG.error("Failed to read banned-players.json", e);
        }

        return players;
    }

    @Override
    public void banPlayer(String playerName, String reason) {
        try {
            String command = "ban " + playerName;
            if (reason != null && !reason.isEmpty()) {
                command += " " + reason;
            }
            consoleWriter.write(command + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to ban player " + playerName, e);
        }
    }

    @Override
    public void unbanPlayer(String playerName) {
        try {
            consoleWriter.write("pardon " + playerName + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to unban player " + playerName, e);
        }
    }
}
