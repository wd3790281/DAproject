package au.edu.unimelb.dingw.game;

import au.edu.unimelb.daassignment.network.NetworkClient;
import au.edu.unimelb.daassignment.network.NetworkHost;
import com.google.common.eventbus.EventBus;

/**
 * Created by dingwang on 16/5/14.
 */
public class Utils {
    // all the things here are singleton and everyone can use in the project

    // the network host used for host to send message
    public static NetworkHost host;
    // client used for client to send message
    public static NetworkClient client;
    // mark the identity of this player
    public static String identity;

    // the event bus used in this project
    public static EventBus bus = new EventBus();
    // if the player win the game
    public static String winOrLose;

    // the flag of the connection
    public static final String CONNECTED_NOTIFICATION = "CONNECTED_NOTIFICATION";

}
