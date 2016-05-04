package au.edu.unimelb.daassignment.network;

import au.edu.unimelb.messages.GameStateExchangeMessage;
import au.edu.unimelb.messages.Message;
import au.edu.unimelb.messages.TimeOffsetDetectMessage;
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

    private String ipAddress;
    private int port;

    public NetworkClient(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void run() {

        // 1. estimate time offset (using NTP-like messages)
        try {
            Socket socket = new Socket(ipAddress, port);
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            for (int i = 0; i < 8; i++) {
                String msgString = reader.readLine();
                long receiveTime = new Date().getTime();
                TimeOffsetDetectMessage msg = gson.fromJson(msgString, TimeOffsetDetectMessage.class);
                if(msg == null) {
                    // Error
                    return;
                }
                TimeOffsetDetectMessage msgToSend = new TimeOffsetDetectMessage();
                msgToSend.lastReceiveTime = receiveTime;
                msgToSend.sentTime = new Date().getTime();
                out.println(gson.toJson(msgToSend));
            }
            // We need to reverse the sign in the client
            long timeOffsetToHost = -Long.parseLong(reader.readLine());
            MessageHandler handler = new MessageHandler(timeOffsetToHost);
            while (!Thread.interrupted()) {
                handler.handleMessage(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NetworkClient("localhost", 3051).run();
    }
}
