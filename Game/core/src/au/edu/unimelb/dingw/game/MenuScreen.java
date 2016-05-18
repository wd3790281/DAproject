package au.edu.unimelb.dingw.game;

import au.edu.unimelb.daassignment.network.NetworkClient;
import au.edu.unimelb.daassignment.network.NetworkHost;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.google.common.eventbus.Subscribe;

import java.io.IOException;

/**
 * Created by dingwang on 16/5/4.
 */
public class MenuScreen implements Screen{

    private Stage stage;
    private Skin skinLibgdx;

    private Game game;
    private Image imgBackground;
    private Window winConnect;
    private Window winWait;
    private TextButton hostButton;
    private TextButton joinButton;
    private TextButton connect;
    private TextButton cancel;
    private TextField ipAddress;

    public MenuScreen (Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // make the stage size 1024*768
        stage = new Stage(new StretchViewport(1024, 768));
        Gdx.input.setInputProcessor(stage);
        rebuildStage();
        Utils.bus.register(this);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skinLibgdx.dispose();
    }

    private void rebuildStage () {

        skinLibgdx = new Skin(Gdx.files.internal("uiskin.json"), new TextureAtlas("uiskin.atlas"));

        // build all layers
        Table layerBackground = buildBackgroundLayer();
        Table option = buildOptWinButtons();
        Table connectWindow = buildConnectWindowLayer();
        Table waitWindow = buildWaitingWindow();
        // assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(1024,768);
        stack.add(option);
        stack.add(layerBackground);
        stage.addActor(connectWindow);
        stage.addActor(waitWindow);


    }

    private Table buildBackgroundLayer () {
        Table layer = new Table();
        // set the layer position at top
        layer.top();
        // + Background image
        imgBackground = new Image(new Texture(Gdx.files.internal("background.png")));
        layer.add(imgBackground);
        return layer;
    }

    private Table buildOptWinButtons () {
        Table tbl = new Table();
        // + host game button
        hostButton = new TextButton("Host Game", skinLibgdx);
        tbl.addActor(hostButton);
        hostButton.setSize(300,50);
        hostButton.setPosition(362, 300);
        hostButton.setColor(0,0,0,1);
        // add event handler
        hostButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onHostGameClicked();
            }
        });
        // + join game button and event handler
        joinButton = new TextButton("Join Game", skinLibgdx);
        tbl.addActor(joinButton);
        joinButton.setSize(300,50);
        joinButton.setPosition(362,200);
        joinButton.setColor(0,0,0,1);
        joinButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onJoinGameClicked();
            }
        });
        return tbl;
    }

    // make waiting for connection window visable
    private void onHostGameClicked(){
        winWait.setVisible(true);
    }

    // make connection window visable
    private void onJoinGameClicked(){
        winConnect.setVisible(true);
    }

    private void onCancelClicked(){
        winConnect.setVisible(false);
        winWait.setVisible(false);
    }

    private Table buildConnectWindowLayer() {
        winConnect = new Window("Connect", skinLibgdx);
        winConnect.setSize(300,300);
        winConnect.setPosition(724,0);

        ipAddress = new TextField("", skinLibgdx);
        ipAddress.setSize(260, 50);
        ipAddress.setPosition(20,150 );
        winConnect.addActor(ipAddress);


        connect = new TextButton("Connect", skinLibgdx);
        connect.setStyle(skinLibgdx.get("connect", TextButton.TextButtonStyle.class));

        connect.setSize(200,40);
        connect.setPosition(50, 100);
        winConnect.addActor(connect);
        connect.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onConnectGameClicked("client");
            }
        });

        cancel = new TextButton("Cancel", skinLibgdx);
        cancel.setStyle(skinLibgdx.get("connect", TextButton.TextButtonStyle.class));
        cancel.setSize(200,40);
        winConnect.addActor(cancel);
        cancel.setPosition(50, 50);
        cancel.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onCancelClicked();
            }
        });

        winConnect.setVisible(false);
        return winConnect;
    }

    // if connect button clicked, open socket for connection
    private void onConnectGameClicked(String identity) {
        if (identity.equals("host")) {
            try {
                Utils.host = new NetworkHost(5000);
                Utils.identity = "host";
                Thread host = new Thread(Utils.host);
                host.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Utils.client = new NetworkClient(ipAddress.getText(), 5000 );
            Utils.identity = "client";
            Thread client = new Thread(Utils.client);
            client.start();

        }
    }

    // build waiting for connection window and make it invisable as default
    private Table buildWaitingWindow(){
        winWait = new Window("Wait", skinLibgdx);
        winWait.setSize(300,300);
        winWait.right().bottom();
        winWait.setVisible(false);

        // + label
        Label label = new Label("Waiting for connection", skinLibgdx);
        label.setStyle(skinLibgdx.get("small", Label.LabelStyle.class));
        label.setSize(200, 40);
        label.setPosition(50, 150);
        winWait.addActor(label);

        // + start game button
        TextButton start = new TextButton("start", skinLibgdx);
        start.setStyle(skinLibgdx.get("connect", TextButton.TextButtonStyle.class));
        start.setSize(200, 40);
        start.setPosition(50, 100);
        start.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onConnectGameClicked("host");
            }
        });
        winWait.addActor(start);

        // + cancel button
        TextButton cancelHost = new TextButton("cancel", skinLibgdx);
        cancelHost.setStyle(skinLibgdx.get("connect", TextButton.TextButtonStyle.class));
        cancelHost.setSize(200, 40);
        cancelHost.setPosition(50, 50);
        cancelHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onCancelClicked();
            }
        });
        winWait.addActor(cancelHost);

        return winWait;
    }

    // once the get the connection succeed flag, the game will start
    @Subscribe
    public void startGame(final String s) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (Utils.CONNECTED_NOTIFICATION.equals(s)) {
                    game.setScreen(new GameScreen(game));
                }
            }
        });

    }
}

