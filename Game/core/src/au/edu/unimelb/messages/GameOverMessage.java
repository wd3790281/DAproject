package au.edu.unimelb.messages;

/**
 * Created by dingwang on 16/5/18.
 */
public class GameOverMessage extends Message {
    public GameOverMessage() {
        this.messageType = Message.MessageType.GameStateExchange;

    }
    public boolean win;
}
