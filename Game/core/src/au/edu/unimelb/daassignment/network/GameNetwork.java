package au.edu.unimelb.daassignment.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import sun.rmi.transport.Connection;

/**
 * Created by HeguangMiao on 1/05/2016.
 */
public class GameNetwork {
    public static void main(String[] args) {
        ServerSocket server = Gdx.net.newServerSocket(Net.Protocol.TCP, 3456, null);
        server.accept(null);

    }
}
