package au.edu.unimelb.messages;

import com.google.gson.Gson;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class TimeOffsetDetectMessage extends Message {
    public TimeOffsetDetectMessage() {
        this.messageType = MessageType.TimeOffsetDetect;
    }

    public long sentTime = -1;
    public long lastReceiveTime = -1;

    @Override
    public String toString() {
        return "s: " + sentTime + " lr: " + lastReceiveTime;
    }
}
