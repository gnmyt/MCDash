package de.gnm.voxeldash.api.handlers;

import de.gnm.voxeldash.VoxelDashLoader;
import de.gnm.voxeldash.api.controller.SessionController;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.IOException;

public class WebSocketHandler implements WebSocketConnectionCallback {

    private final VoxelDashLoader loader;

    /**
     * Basic constructor of the {@link WebSocketHandler}
     *
     * @param loader The loader
     */
    public WebSocketHandler(VoxelDashLoader loader) {
        this.loader = loader;
    }

    /**
     * Called when a new WebSocket connection is established.
     *
     * @param exchange The exchange
     * @param channel  The channel
     */
    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        SessionController sessionController = loader.getController(SessionController.class);

        if (!exchange.getRequestParameters().containsKey("sessionToken")) {
            closeSession(channel, 4001, "No session token provided");
            return;
        }

        String sessionToken = exchange.getRequestParameters().get("sessionToken").get(0);

        if (!sessionController.isValidToken(sessionToken)) {
            closeSession(channel, 4002, "Invalid session token");
            return;
        }

        channel.getReceiveSetter().set(new WebSocketReceiver(loader));
        channel.resumeReceives();
    }

    /**
     * Closes the session with the given code and reason.
     *
     * @param channel The channel
     * @param code    The code
     * @param reason  The reason
     */
    public void closeSession(WebSocketChannel channel, int code, String reason) {
        channel.setCloseReason(reason);
        channel.setCloseCode(code);
        try {
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
