package au.edu.unimelb.algorithms;

import au.edu.unimelb.messages.GameStateExchangeMessage;
import au.edu.unimelb.utils.CircularArray;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Bucket Synchronization Algorithm
 *
 * You should use the singleton object.
 *
 * Created by HeguangMiao on 3/05/2016.
 */
public class BucketManager {
    private class Bucket {
        long startTime;
        long endTime = -1;

        private HashMap<Integer, GameStateExchangeMessage> messageMap = new HashMap<Integer, GameStateExchangeMessage>();

        public synchronized void put(GameStateExchangeMessage message, Integer sourceId) {
            // only if the message's time stamp is within the bucket time slot, can a message be put in
            if (message.timeStamp < startTime && (endTime == -1 || message.timeStamp > endTime) ) {
                return;
            }
            GameStateExchangeMessage existMessage = messageMap.get(sourceId);
            if (existMessage == null || existMessage.timeStamp < message.timeStamp) {
                // According to the bucket synchronization algorithm, the bucket only holds
                // the newest message for a sender.
                // If there is an old message, overwrite the old one.
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

        @Override
        public String toString() {
            return messageMap.toString();
        }
    }


    private CircularArray<Bucket> buckets;

    public static final BucketManager defaultManager = new BucketManager(3);


    public BucketManager(int bufferSize) {
        buckets = new CircularArray<Bucket>(bufferSize);
    }

    /**
     * Get messages for this time slot
     *
     * When this method is called, it indicates the end of
     * the last time slot and the start of the new time slot.
     *
     * @return messages for this time slot. The first 3 calls will return null.
     */
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
        buckets.add(b); // Not normal add, please see CircularArray.add for detailed information
//        System.out.println(messages);
        return messages;
    }

    /**
     * Put message into buckets.
     * @param message
     * @param sourceId the id of the sender
     */
    public void receiveMessage(GameStateExchangeMessage message, int sourceId) {
        Object[] allBuckets = buckets.allElements();
        for (int i = 0; i< buckets.size(); i++) {
            Bucket b = (Bucket)allBuckets[i];
            // only message timestamp that is within the time slot of the bucket can be put into the bucket.
            // See Bucket -> put() for more information.
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
