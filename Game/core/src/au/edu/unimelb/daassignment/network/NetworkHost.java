package au.edu.unimelb.daassignment.network;

import au.edu.unimelb.messages.TimeOffsetDetectMessage;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class NetworkHost implements Runnable {

    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader reader;
    private Gson gson = new Gson();

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

            for (int i = 0; i < 8; i++) {
                System.out.print(sentMessages[i] + "\t");
                System.out.println(receivedMessages[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new NetworkHost(3051).run();
    }
}
