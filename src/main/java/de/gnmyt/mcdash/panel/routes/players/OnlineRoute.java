package de.gnmyt.mcdash.panel.routes.players;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class OnlineRoute extends DefaultHandler {

    @Override
    public String path() {
        return "online";
    }

    /**
     * Gets all current online players
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {

        ArrayBuilder builder = new ArrayBuilder();

        Statistic playStat;
        try {
            playStat = Statistic.valueOf("PLAY_ONE_TICK"); // used below MC 1.15.2
        } catch (IllegalArgumentException ignored) {
            playStat = Statistic.valueOf("PLAY_ONE_MINUTE"); // MC 1.15.2 and above
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            builder.addNode()
                    .add("uuid", player.getUniqueId().toString())
                    .add("name", player.getName())
                    .add("player_time", player.getStatistic(playStat))
                    .add("current_world", player.getWorld().getName())
                    .add("address", player.getAddress().getHostName())
                    .add("health", Math.round(player.getHealth()))
                    .add("food_level", Math.round(player.getFoodLevel()))
                    .add("game_mode", player.getGameMode().name())
                    .add("is_op", player.isOp())
                    .register();
        }

        response.type(ContentType.JSON).text(builder.toJSON());
    }
}
