package au.edu.unimelb.messages;

import com.google.gson.Gson;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class TimeOffsetDetectMessage extends Message {
    public TimeOffsetDetectMessage() {
        this.messageType = MessageType.TimeOffsetDetect;
    }

    public double lastMessageSentTime;
    public double lastMessageReceiveTime;
    public double sentTime;

    public static void main(String[] args) {
        Gson gson = new Gson();
        String json = gson.toJson(new TimeOffsetDetectMessage());
        TimeOffsetDetectMessage msg2 = gson.fromJson(json, TimeOffsetDetectMessage.class);
        System.out.println(msg2.messageType);
    }
}
