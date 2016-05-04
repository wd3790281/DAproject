package au.edu.unimelb.messages;

import com.google.gson.JsonObject;

/**
 * Created by HeguangMiao on 2/05/2016.
 */
public class GameStateExchangeMessage extends Message {

    public GameStateExchangeMessage() {
        this.messageType = MessageType.GameStateExchange;
    }

    public JsonObject extraInfo;
}
