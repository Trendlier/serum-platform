package serum.controller;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import static play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class ThreadSocketController extends Controller {
    private static class Thing
    {
        public String value;
        public List<String> values;
    }

    public static WebSocket<JsonNode> socket()
    {
        return new WebSocket<JsonNode>() {
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) 
            {
                // For each event received on the socket,
                in.onMessage(new Callback<JsonNode>() {
                    public void invoke(JsonNode json) {
                        // Log events to the console
                        Thing thing = fromJson(json, Thing.class);
                        System.out.println("Value: " + thing.value);
                        System.out.println("Values: " + thing.values);
                    } 
                });

                // When the socket is closed.
                in.onClose(new Callback0() {
                     public void invoke() {
                         System.out.println("Disconnected");
                     }
                });

                // Send a single 'Hello!' message
                JsonNode json = toJson(
                    new Thing() {
                        public String value = "hello, world";
                        public List<String> values = Arrays.asList(new String[] {"hello", "world"});
                    }
                );
                out.write(json);
            }
        };
    }
}
