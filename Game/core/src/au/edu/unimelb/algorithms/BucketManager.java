package au.edu.unimelb.algorithms;

import au.edu.unimelb.messages.GameStateExchangeMessage;
import au.edu.unimelb.utils.CircularArray;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HeguangMiao on 3/05/2016.
 */
public class BucketManager {
    class Bucket {
        long startTime;
        long endTime = -1;

        private HashMap<Integer, GameStateExchangeMessage> messageMap = new HashMap<Integer, GameStateExchangeMessage>();

        public synchronized void put(GameStateExchangeMessage message, Integer sourceId) {
            if (message.timeStamp < startTime && (endTime == -1 || message.timeStamp > endTime) ) {
                return;
            }
            GameStateExchangeMessage existMessage = messageMap.get(sourceId);
            if (existMessage == null || existMessage.timeStamp < message.timeStamp) {
                messageMap.put(sourceId, message);
            }
        }

        public GameStateExchangeMessage[] getMessages() {
            int size = messageMap.size();
            GameStateExchangeMessage[] messages = new GameStateExchangeMessage[size];
            int i = 0;
            for (GameStateExchangeMessage message : messageMap.values()) {
                messages[i] = message;
                i++;
            }
            return messages;
        }

        // for reuse
        public void reset() {
            startTime = 0;
            endTime = -1;
            messageMap.clear();
        }
    }

    private CircularArray<Bucket> buckets;


    public BucketManager(int bufferSize) {
        buckets = new CircularArray<Bucket>(bufferSize);
    }

    public GameStateExchangeMessage[] getMessages() {
        Bucket b = buckets.getOldest();
        if (b == null) {
            return null;
        }
        GameStateExchangeMessage[] messages = b.getMessages();
        Bucket latestBucket = buckets.getNewest();
        long timeStamp = new Date().getTime();
        // The start time of new bucket is the end time of last bucket
        if (latestBucket != null) {
            latestBucket.endTime = timeStamp;
        }
        // reuse bucket
        b.reset();
        b.startTime = timeStamp;
        buckets.add(b);
        return messages;
    }

    public void receiveMessage(GameStateExchangeMessage message, int sourceId) {
        Object[] allBuckets = buckets.allElements();
        for (Object obj : allBuckets) {
            Bucket b = (Bucket)obj;
            b.put(message, sourceId);
        }
    }
}
