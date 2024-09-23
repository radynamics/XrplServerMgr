package com.radynamics.xrplservermgr.xrpl.subscription;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;

@WebSocket
public class SubscriptionStreamSession {
    private final URI endpoint;
    private final StreamListener listener;

    private Session session;

    public SubscriptionStreamSession(URI endpoint, StreamListener listener) {
        this.endpoint = endpoint;
        this.listener = listener;

        org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
    }

    public void subscribe() {
        final WebSocketClient client = new WebSocketClient();
        try {
            client.start();

            var request = new ClientUpgradeRequest();
            var fut = client.connect(this, endpoint, request);
            session = fut.get();

            sendCommand("subscribe");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendCommand(String command) {
        session.getRemote().sendStringByFuture("{ \"id\": \"watch xrpl streams\", \"command\": \"%s\", \"streams\": [ \"%s\" ] }".formatted(command, listener.channelName()));
    }

    public void unsubscribe(StreamListener l) {
        sendCommand("unsubscribe");
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        var json = (JsonObject) JsonParser.parseString(message);
        // First response message is the register command.
        if (json.has("id")) return;
        listener.onReceive(json);
    }
}
