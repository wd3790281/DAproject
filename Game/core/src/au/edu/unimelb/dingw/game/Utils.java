package au.edu.unimelb.dingw.game;

import au.edu.unimelb.daassignment.network.NetworkClient;
import au.edu.unimelb.daassignment.network.NetworkHost;
import com.google.common.eventbus.EventBus;

/**
 * Created by dingwang on 16/5/14.
 */
public class Utils {

    public static NetworkHost host;
    public static NetworkClient client;
    public static String identity;
    public static EventBus bus = new EventBus();

}
