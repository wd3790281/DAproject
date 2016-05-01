package au.edu.unimelb.daassignment.network;

import au.edu.unimelb.messages.LatencyDetectMessage;
import au.edu.unimelb.messages.Message;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class NetworkClient implements Runnable{

    private Socket socket;
    private PrintWriter out;
    private BufferedReader reader;
    private Gson gson = new Gson();

    private class MessageHandler {
        public void handleMessage(String messageString) {
            Message msg = gson.fromJson(messageString, Message.class);
            switch (msg.messageType) {
                case LatencyDetect:
                    LatencyDetectMessage latencyMessage = gson.fromJson(messageString, LatencyDetectMessage.class);
                    handleLatencyDetectMessage(latencyMessage);
                    break;
            }
        }

        public void handleLatencyDetectMessage(LatencyDetectMessage message) {

        }
    }

    public NetworkClient(String ipAddress, int port) throws IOException {
        Socket socket = new Socket(ipAddress, port);
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        // 1. do latency testing
        try {
            double sumLatency = 0;
            long timeStamp = new Date().getTime();
            for (int i = 0; i < 4; i++) {
                String msgJSON = reader.readLine();
                long timeElapse = new Date().getTime() - timeStamp;
                if(i != 0) {
                    // We ignore the first call because of uncertainty.
                    sumLatency += timeElapse;
                }
                LatencyDetectMessage msg = new LatencyDetectMessage();
                msg.seq = 0; // Actually we don't have to bother the seq because it's running on TCP
                out.println(gson.toJson(msg));
                timeStamp = new Date().getTime();
            }
            sumLatency /= 3;



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
