package au.edu.unimelb.messages;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class LatencyDetectMessage extends Message {
    public int seq = 0;

    public LatencyDetectMessage() {
        this.messageType = MessageType.LatencyDetect;
    }
}
