package au.edu.unimelb.daassignment.network;

import au.edu.unimelb.algorithms.BucketManager;
import au.edu.unimelb.dingw.game.Utils;
import au.edu.unimelb.messages.GameOverMessage;
import au.edu.unimelb.messages.GameStateExchangeMessage;
import au.edu.unimelb.messages.Message;
import com.badlogic.gdx.Game;
import com.google.gson.Gson;

/**
 * Created by HeguangMiao on 4/05/2016.
 */
public class MessageHandler {
    private long timeOffset = Long.MAX_VALUE;
    private Gson gson = new Gson();

    public MessageHandler(long timeOffset) {
        this.timeOffset = timeOffset;
    }

    public void handleMessage(String messageString) {
        Message msg = gson.fromJson(messageString, Message.class);
        switch (msg.messageType) {
            case GameStateExchange:
                handleGameStateExchangeMessage(gson.fromJson(messageString, GameStateExchangeMessage.class));
                break;
            default:
                break;
        }
    }
    public void handleGameStateExchangeMessage(GameStateExchangeMessage msg) {
        msg.timeStamp -= timeOffset;
        BucketManager.defaultManager.receiveMessage(msg, 1); // received message source must be 1. local source is 0.
    }

    public void handleGameOverMessage(GameOverMessage msg) {
        msg.timeStamp -= timeOffset;
        Utils.bus.post(msg);
    }

}