package au.edu.unimelb.messages;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class Message {
    public enum MessageType {
        TimeOffsetDetect,
        LatencyDetect
    }

    public MessageType messageType;
}
