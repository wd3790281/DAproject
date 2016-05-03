package au.edu.unimelb.messages;

import com.google.gson.Gson;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class Message {
    public enum MessageType {
        TimeOffsetDetect,
        GameStateExchange
    }

    public MessageType messageType;
    public long timeStamp;
}
