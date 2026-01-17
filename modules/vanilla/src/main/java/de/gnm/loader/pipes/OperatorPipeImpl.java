package de.gnm.loader.pipes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.gnm.voxeldash.api.entities.OfflinePlayer;
import de.gnm.voxeldash.api.pipes.players.OperatorPipe;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class OperatorPipeImpl implements OperatorPipe {

    private static final Logger LOG = LogManager.getLogger("VoxelDashVanilla");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String OPS_JSON_PATH = "ops.json";

    private final BufferedWriter consoleWriter;

    public OperatorPipeImpl(OutputStream console) {
        this.consoleWriter = new BufferedWriter(new OutputStreamWriter(console));
    }

    @Override
    public ArrayList<OfflinePlayer> getOperators() {
        ArrayList<OfflinePlayer> operators = new ArrayList<>();
        try {
            File opsFile = new File(OPS_JSON_PATH);
            if (!opsFile.exists()) {
                LOG.warn("ops.json file not found at " + OPS_JSON_PATH);
                return operators;
            }

            ArrayNode ops = (ArrayNode) MAPPER.readTree(opsFile);
            for (JsonNode op : ops) {
                operators.add(new OfflinePlayer(op.get("name").asText(), op.get("uuid").asText()));
            }

            return operators;

        } catch (Exception e) {
            LOG.error("Failed to read operators from ops.json", e);
        }
        return operators;
    }

    @Override
    public void setOp(String playerName) {
        try {
            consoleWriter.write("op " + playerName + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to set " + playerName + " as operator", e);
        }
    }

    @Override
    public void deOp(String playerName) {
        try {
            consoleWriter.write("deop " + playerName + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to deop " + playerName, e);
        }
    }
}
