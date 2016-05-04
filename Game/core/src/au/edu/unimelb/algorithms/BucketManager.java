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
    private class Bucket {
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

        public HashMap<Integer, GameStateExchangeMessage> getMessages() {
            return messageMap;
        }

        // for reuse
        public void reset() {
            startTime = 0;
            endTime = -1;
            messageMap.clear();
        }
    }

    private CircularArray<Bucket> buckets;

    public static final BucketManager defaultManager = new BucketManager(3);


    public BucketManager(int bufferSize) {
        buckets = new CircularArray<Bucket>(bufferSize);
    }

    public HashMap<Integer, GameStateExchangeMessage> getMessages() {
        long timeStamp = new Date().getTime();
        if(buckets.size() < buckets.getCapacity()) {
            // create new bucket
            Bucket newBucket = new Bucket();
            newBucket.startTime = timeStamp;
            buckets.add(newBucket);
            return null;
        }
        Bucket b = buckets.getOldest();
        if (b == null) {
            return null;
        }
        // return a copy
        HashMap<Integer, GameStateExchangeMessage> messages = new HashMap<Integer, GameStateExchangeMessage>(b.getMessages());
        Bucket latestBucket = buckets.getNewest();

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
        for (int i = 0; i< buckets.size(); i++) {
            Bucket b = (Bucket)allBuckets[i];
            b.put(message, sourceId);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BucketManager man = new BucketManager(3);
        man.getMessages();
        long timeStamp = new Date().getTime();
        GameStateExchangeMessage msg = new GameStateExchangeMessage();
        msg.timeStamp = timeStamp;
        man.receiveMessage(msg,1);
        Thread.sleep(10);
        HashMap<Integer, GameStateExchangeMessage> messages = man.getMessages();
        assert messages == null;
        Thread.sleep(10);
        messages = man.getMessages();
        Thread.sleep(10);
        messages = man.getMessages();
        assert messages.get(1) != null;

    }
}
