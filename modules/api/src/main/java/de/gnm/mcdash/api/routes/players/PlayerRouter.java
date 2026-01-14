package de.gnm.mcdash.api.routes.players;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.entities.BannedPlayer;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.OfflinePlayer;
import de.gnm.mcdash.api.entities.OnlinePlayer;
import de.gnm.mcdash.api.entities.PermissionLevel;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.pipes.players.BanPipe;
import de.gnm.mcdash.api.pipes.players.OnlinePlayerPipe;
import de.gnm.mcdash.api.pipes.players.OperatorPipe;
import de.gnm.mcdash.api.pipes.players.WhitelistPipe;
import de.gnm.mcdash.api.routes.BaseRoute;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class PlayerRouter extends BaseRoute {

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Players)
    @Path("/players/online")
    @Method(GET)
    public JSONResponse getOnlinePlayers() {
        OnlinePlayerPipe pipe = getPipe(OnlinePlayerPipe.class);
        ArrayNode players = getMapper().createArrayNode();

        for (OnlinePlayer player : pipe.getOnlinePlayers()) {
            ObjectNode playerNode = getMapper().createObjectNode();
            playerNode.put("name", player.getName());
            playerNode.put("uuid", player.getUuid().toString());
            playerNode.put("world", player.getWorld());
            playerNode.put("ipAddress", player.getIpAddress());
            playerNode.put("health", player.getHealth());
            playerNode.put("hunger", player.getHunger());
            playerNode.put("op", player.isOp());
            playerNode.put("gamemode", player.getGamemode());
            playerNode.put("playtime", player.getPlaytime());
            players.add(playerNode);
        }

        return new JSONResponse().add("players", players);
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/kick")
    @Method(POST)
    public JSONResponse kickPlayer(JSONRequest request) {
        request.checkFor("playerName");
        String playerName = request.get("playerName");
        String reason = request.has("reason") ? request.get("reason") : "Kicked by administrator";

        OnlinePlayerPipe pipe = getPipe(OnlinePlayerPipe.class);
        pipe.kickPlayer(playerName, reason);

        return new JSONResponse().message("Player kicked");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/gamemode")
    @Method(POST)
    public JSONResponse setGamemode(JSONRequest request) {
        request.checkFor("playerName", "gamemode");
        String playerName = request.get("playerName");
        String gamemode = request.get("gamemode");

        OnlinePlayerPipe pipe = getPipe(OnlinePlayerPipe.class);
        pipe.setGamemode(playerName, gamemode);

        return new JSONResponse().message("Gamemode changed");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/teleport")
    @Method(POST)
    public JSONResponse teleportToWorld(JSONRequest request) {
        request.checkFor("playerName", "worldName");
        String playerName = request.get("playerName");
        String worldName = request.get("worldName");

        OnlinePlayerPipe pipe = getPipe(OnlinePlayerPipe.class);
        pipe.teleportToWorld(playerName, worldName);

        return new JSONResponse().message("Player teleported");
    }


    @AuthenticatedRoute
    @RequiresFeatures(Feature.Players)
    @Path("/players/whitelist")
    @Method(GET)
    public JSONResponse getWhitelistedPlayers() {
        WhitelistPipe pipe = getPipe(WhitelistPipe.class);
        ArrayNode players = getMapper().createArrayNode();

        for (OfflinePlayer player : pipe.getWhitelistedPlayers()) {
            ObjectNode playerNode = getMapper().createObjectNode();
            playerNode.put("name", player.getName());
            playerNode.put("uuid", player.getUuid().toString());
            players.add(playerNode);
        }

        return new JSONResponse()
                .add("players", players)
                .add("enabled", pipe.getStatus());
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/whitelist/status")
    @Method(POST)
    public JSONResponse setWhitelistStatus(JSONRequest request) {
        request.checkFor("enabled");
        boolean enabled = request.getBoolean("enabled");

        WhitelistPipe pipe = getPipe(WhitelistPipe.class);
        pipe.setStatus(enabled);

        return new JSONResponse().message("Whitelist status updated");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/whitelist/add")
    @Method(POST)
    public JSONResponse addToWhitelist(JSONRequest request) {
        request.checkFor("playerName");
        String playerName = request.get("playerName");

        WhitelistPipe pipe = getPipe(WhitelistPipe.class);
        pipe.addPlayer(playerName);

        return new JSONResponse().message("Player added to whitelist");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/whitelist/remove")
    @Method(POST)
    public JSONResponse removeFromWhitelist(JSONRequest request) {
        request.checkFor("playerName");
        String playerName = request.get("playerName");

        WhitelistPipe pipe = getPipe(WhitelistPipe.class);
        pipe.removePlayer(playerName);

        return new JSONResponse().message("Player removed from whitelist");
    }


    @AuthenticatedRoute
    @RequiresFeatures(Feature.Players)
    @Path("/players/banned")
    @Method(GET)
    public JSONResponse getBannedPlayers() {
        BanPipe pipe = getPipe(BanPipe.class);
        ArrayNode players = getMapper().createArrayNode();

        for (BannedPlayer player : pipe.getBannedPlayers()) {
            ObjectNode playerNode = getMapper().createObjectNode();
            playerNode.put("name", player.getName());
            playerNode.put("uuid", player.getUuid().toString());
            playerNode.put("reason", player.getReason());
            playerNode.put("banDate", player.getBanDate() != null ? player.getBanDate().getTime() : null);
            playerNode.put("expiry", player.getExpiry() != null ? player.getExpiry().getTime() : null);
            playerNode.put("source", player.getSource());
            players.add(playerNode);
        }

        return new JSONResponse().add("players", players);
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/ban")
    @Method(POST)
    public JSONResponse banPlayer(JSONRequest request) {
        request.checkFor("playerName");
        String playerName = request.get("playerName");
        String reason = request.has("reason") ? request.get("reason") : "Banned by administrator";

        BanPipe pipe = getPipe(BanPipe.class);
        pipe.banPlayer(playerName, reason);

        return new JSONResponse().message("Player banned");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/unban")
    @Method(POST)
    public JSONResponse unbanPlayer(JSONRequest request) {
        request.checkFor("playerName");
        String playerName = request.get("playerName");

        BanPipe pipe = getPipe(BanPipe.class);
        pipe.unbanPlayer(playerName);

        return new JSONResponse().message("Player unbanned");
    }


    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Players, level = PermissionLevel.FULL)
    @Path("/players/op")
    @Method(POST)
    public JSONResponse setOperator(JSONRequest request) {
        request.checkFor("playerName", "op");
        String playerName = request.get("playerName");
        boolean op = request.getBoolean("op");

        OperatorPipe pipe = getPipe(OperatorPipe.class);
        if (op) {
            pipe.setOp(playerName);
        } else {
            pipe.deOp(playerName);
        }

        return new JSONResponse().message("Operator status updated");
    }

}
