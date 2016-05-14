package au.edu.unimelb.daassignment.network;

import au.edu.unimelb.dingw.game.Utils;
import au.edu.unimelb.messages.Message;
import au.edu.unimelb.messages.TimeOffsetDetectMessage;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class NetworkHost implements Runnable {

    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader reader;
    private Gson gson = new Gson();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public NetworkHost(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            this.socket = this.serverSocket.accept();
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            // 1. time offset estimation
            TimeOffsetDetectMessage[] sentMessages = new TimeOffsetDetectMessage[8];
            TimeOffsetDetectMessage[] receivedMessages = new TimeOffsetDetectMessage[8];
            long[] localMessageReceiveTimes = new long[8];

            for (int i = 0; i < 8; i++) {
                TimeOffsetDetectMessage msgToSend = new TimeOffsetDetectMessage();
                if (i != 0) {
                    msgToSend.lastReceiveTime = localMessageReceiveTimes[i-1];
                }
                msgToSend.sentTime = new Date().getTime();
                out.println(gson.toJson(msgToSend));
                sentMessages[i] = msgToSend;
                String msgString = reader.readLine();
                long receiveTime = new Date().getTime();
                localMessageReceiveTimes[i] = receiveTime;
                TimeOffsetDetectMessage msg = gson.fromJson(msgString, TimeOffsetDetectMessage.class);
                if(msg == null) {
                    // Error
                    return;
                }
                receivedMessages[i] = msg;
            }
            int minDelayIndex = 0;
            long minDelay = receivedMessages[0].lastReceiveTime
                    - sentMessages[0].sentTime
                    + localMessageReceiveTimes[0]
                    - receivedMessages[0].sentTime;

            for (int i = 1; i < 8; i++) {
                long delay = receivedMessages[i].lastReceiveTime
                        - sentMessages[i].sentTime
                        + localMessageReceiveTimes[i]
                        - receivedMessages[i].sentTime;
                if(delay < minDelay) {
                    minDelay = delay;
                    minDelayIndex = i;
                }
            }
            long d = receivedMessages[minDelayIndex].lastReceiveTime
                    - sentMessages[minDelayIndex].sentTime
                    + localMessageReceiveTimes[minDelayIndex]
                    - receivedMessages[minDelayIndex].sentTime;
            long oi =  receivedMessages[minDelayIndex].lastReceiveTime
                    - sentMessages[minDelayIndex].sentTime
                    - localMessageReceiveTimes[minDelayIndex]
                    + receivedMessages[minDelayIndex].sentTime;
            long timeOffsetToClient = ((oi - d/2) + (oi + d/2)) / 2;
            // here we tell the client the result
            out.println(timeOffsetToClient);
            out.flush();
//            System.out.println(timeOffsetToClient);
            Utils.bus.post(Utils.CONNECTED_NOTIFICATION);
            MessageHandler handler = new MessageHandler(timeOffsetToClient);
            while (!Thread.interrupted()) {
                handler.handleMessage(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(final Message message) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String json = gson.toJson(message);
                out.println(json);
                out.flush();
            }
        });
    }

    public static void main(String[] args) throws IOException {
        new NetworkHost(3051).run();
    }
}
