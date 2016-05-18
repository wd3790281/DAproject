package au.edu.unimelb.dingw.game;

import au.edu.unimelb.algorithms.BucketManager;
import au.edu.unimelb.messages.GameOverMessage;
import au.edu.unimelb.messages.GameStateExchangeMessage;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;

/**
 * Created by dingwang on 16/5/4.
 */

// this class is the game screen, it is in charge of the game drawing and logic
public class GameScreen implements Screen {

    private Game game;

    private Texture tankImage;  // initial tank image
    private Texture wallImage;  // the wall image
    private int mDirection;   // initial tank direction for mytank
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Tank myTank;
    private Tank enemyTank;
    private Array<Bullet> bullets;    // all bullets are in this array
    private Array<Rectangle> walls;   // containing all walls

    private Array<Integer> myPressedKeys;   // this is used to record all the movement key in one delta time

    private GameStateExchangeMessage message;   // message going to be sent
    private HashMap<Integer, GameStateExchangeMessage> exchangeMessageHashMap;
    private GameStateExchangeMessage enemyMessage;
    private GameStateExchangeMessage myMessage;
    private Array<Integer> myFireAction;
    private JsonArray myMovement;
    private JsonArray enemyMovement;
    private JsonArray myFire;
    private JsonArray enemyFire;

    public GameScreen(Game game) {
        this.game = game;
    }


    @Override
    public void show() {

        // load wall and tank image for resources
        wallImage = new Texture(Gdx.files.internal("commonWall.gif"));
        tankImage = new Texture(Gdx.files.internal("tankR.gif"));
        Texture enemyTankImage = new Texture(Gdx.files.internal("tankL.gif"));

        // define default direction of my tank to right
        mDirection = 2;
        message = new GameStateExchangeMessage();

        // register this class in event bus
        Utils.bus.register(this);

        // game screen is 1024 * 768
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 768);
        batch = new SpriteBatch();


        // initial the tank according to different identity, host tank in bottom
        // facing right, joiner tank is in top and facing left
        if (Utils.identity.equals("host")) {
            myTank = new Tank(tankImage, mDirection, 492, 20);
            enemyTank = new Tank(enemyTankImage, 1, 492, 700);
        } else {
            myTank = new Tank(enemyTankImage, 1, 492, 700);
            enemyTank = new Tank(tankImage, mDirection, 492, 20);
        }

        bullets = new Array<Bullet>();

        myPressedKeys = new Array<Integer>();
        myFireAction = new Array<Integer>();
        myFire = new JsonArray();
        myMovement = new JsonArray();
        enemyFire = new JsonArray();
        enemyMovement = new JsonArray();

        // initialize walls on the screen
        walls = new Array<Rectangle>();
        for (float y = 0; y < 768; y += 21) {
            createWall(100, y);
        }
        for (float y = 0; y < 768; y += 21) {
            createWall(y, 100);
        }
    }

    // this method is used to create wall in certain position
    private void createWall(float x, float y) {
        Rectangle wall = new Rectangle();
        wall.x = x;
        wall.y = y;
        wall.width = 22;
        wall.height = 21;
        walls.add(wall);
    }

    // render will loop when the screen is present, the method below will be
    // executed between frames. Delta time means the time between two frames
    @Override
    public void render(float delta) {

        // background is black
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        // load the latest game state from the bucket
        updateGameState();

        // begin to draw everything in the screen
        batch.begin();

        batch.draw(myTank.getTankImage(), myTank.getTank().getX(), myTank.getTank().getY());
        batch.draw(enemyTank.getTankImage(), enemyTank.getTank().getX(), enemyTank.getTank().getY());
        for (Bullet bullet : bullets) {
            batch.draw(bullet.getBulletImage(), bullet.getBullet().x, bullet.getBullet().y);
        }

        for (Rectangle wall : walls) {
            batch.draw(wallImage, wall.x, wall.y);
        }
        batch.end();

        // after taking the latest update from bucket, act the action locally
        action();

        // clear my action at the beginning of the render, save the action and then send to bucket
        myPressedKeys.clear();
        myFireAction.clear();

        // detect tank movement, 1
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                myPressedKeys.add(5);
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                myPressedKeys.add(7);
            } else {
                myPressedKeys.add(1);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                myPressedKeys.add(6);
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                myPressedKeys.add(8);
            } else {
                myPressedKeys.add(2);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                myPressedKeys.add(5);
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                myPressedKeys.add(6);
            } else {
                myPressedKeys.add(3);
            }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                myPressedKeys.add(7);
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                myPressedKeys.add(8);
            } else {
                myPressedKeys.add(4);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            myFireAction.add(1);
        }


        // iterate the bullets array, detect if bullet collides with other element, such as
        // tanks or walls. if happened, remove from bullets

        for (int i = 0; i < bullets.size; i++) {

            Bullet bullet = bullets.get(i);

            // update every bullet position in the screen
            bullet.changeBulletPosition();

            // if the bullet has been out of the screen, remove it from the arry
            if (bullet.outOfScreen()) {
                bullets.removeIndex(i);
                continue;
            }

            // detect whether the tank has collided with any tank, if the bullet hit mytank, I lose
            // turn to the end screen and send game over message to the enemy
            if (bullet.getBullet().overlaps(myTank.getTank())) {
                bullets.removeIndex(i);
                Utils.winOrLose = "lose";
                game.setScreen(new EndScreen(game));
                sendGameOverMessage(true);

            } else if (bullet.getBullet().overlaps(enemyTank.getTank())) {
                bullets.removeIndex(i);
                Utils.winOrLose = "win";
                game.setScreen(new EndScreen(game));
                sendGameOverMessage(false);
            }

            // detect whether the bullet hit the wall, if it is, remove both of them from the screen
            for (int j = 0; j < walls.size; j++) {
                Rectangle wall = walls.get(j);
                if (bullet.getBullet().overlaps(wall) || wall.overlaps(bullet.getBullet()) || wall.contains(bullet.getBullet())) {
                    bullets.removeIndex(i);
                    walls.removeIndex(j);

                }

            }
        }

        // detect whether the tank has collided with the wall, if it is, restrict the tank movement.
        for (int j = 0; j < walls.size; j++) {
            Rectangle wall = walls.get(j);
            if (wall.overlaps(myTank.getTank()) || myTank.getTank().overlaps(wall)) {
                myTank.restrictTank();
            }
        }

        // send all my movement saved in myPressedKeys and myFireAction to the other player
        sendMessage();

    }

    // this class has register on event bus, once the anyone post a bullet, it will receive the
    // bullet and put it in the bullets array
    @Subscribe
    private void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    // this method is used to send GameStateMessage, the message will be sent in json
    private void sendMessage() {

        // this message will contain my movement and fire action in this delta time,
        // it will also send mytank's current position, direction for the counter part
        // to do dead rekon
        JsonObject jsonObject = new JsonObject();

        // send my movement in this delta time
        JsonArray myMove = new JsonArray();
        for (Integer i : myPressedKeys) {
            myMove.add(i);
        }
        jsonObject.add(Utils.identity, myMove);

        // send my fire action in this delta time
        JsonArray fire = new JsonArray();

        for (Integer j : myFireAction) {
            fire.add(j);
        }
        jsonObject.add(Utils.identity + "f", fire);

        // send my tank's current postion and direction
        jsonObject.addProperty(Utils.identity + "x", myTank.getTank().x);
        jsonObject.addProperty(Utils.identity + "y", myTank.getTank().y);
        jsonObject.addProperty(Utils.identity + "d", myTank.getDirection());
        message.extraInfo = jsonObject;

        // send message to the other player's bucket
        if (Utils.identity.equals("host")) {
            Utils.host.send(message);
        } else {
            Utils.client.send(message);
        }

        // send my movement to my own bucket manager
        BucketManager.defaultManager.receiveMessage(message, 0);
    }

    // get the latest game state from the bucket, all the element state and action should
    // get from bucket, mytank get from my bucket, enemy tank get from enemy bucket
    private void updateGameState(){

        // get the game state message from bucket manager
        exchangeMessageHashMap = BucketManager.defaultManager.getMessages();

        // first 3 bucket is empty, so exchangeMessageHashMap can be empty
        // then this part is used for dead rekon. enemytank's state will
        // be update inner if. If the other play disconnect from the game,
        // the exchangeMessage will be empty, the enemy tank will be computed
        // from the last action, the last information fo the enemy tank will be
        // left for local computing.

        if (exchangeMessageHashMap != null) {

            // get enemy tank's state from message
            enemyMessage = exchangeMessageHashMap.get(1);

            if (enemyMessage != null) {

                // judge the key word according to my identity, in order to get
                // the enemy tank's state from the message
                if (Utils.identity.equals("host")) {

                    if (enemyMessage.extraInfo.get("clientx") != null
                            && enemyMessage.extraInfo.get("clienty") != null
                            && enemyMessage.extraInfo.get("clientd") != null ) {

                        enemyTank.updateTankState(enemyMessage.extraInfo.get("clientx").getAsFloat(),
                                enemyMessage.extraInfo.get("clienty").getAsFloat(),
                                enemyMessage.extraInfo.get("clientd").getAsInt());

                        enemyMovement = enemyMessage.extraInfo.getAsJsonArray("client");
                        enemyFire = enemyMessage.extraInfo.getAsJsonArray("clientf");
                    }
                } else {
                    if (enemyMessage.extraInfo.get("hostx") != null
                            && enemyMessage.extraInfo.get("hosty") != null
                            && enemyMessage.extraInfo.get("hostd") != null ) {

                        enemyTank.updateTankState(enemyMessage.extraInfo.get("hostx").getAsFloat(),
                                enemyMessage.extraInfo.get("hosty").getAsFloat(),
                                enemyMessage.extraInfo.get("hostd").getAsInt());

                        enemyMovement = enemyMessage.extraInfo.getAsJsonArray("host");
                        enemyFire = enemyMessage.extraInfo.getAsJsonArray("hostf");
                    }
                }
            }

            // get my tank's latest state and action from bucket manager
            myMessage = exchangeMessageHashMap.get(0);

            if (myMessage != null) {
                if (myMessage.extraInfo.get(Utils.identity + "x") != null
                        && myMessage.extraInfo.get(Utils.identity + "y") != null
                        && myMessage.extraInfo.get(Utils.identity + "d") != null)
                myTank.updateTankState(myMessage.extraInfo.get(Utils.identity + "x").getAsFloat(),
                        myMessage.extraInfo.get(Utils.identity + "y").getAsFloat(),
                        myMessage.extraInfo.get(Utils.identity + "d").getAsInt());

                myMovement = myMessage.extraInfo.getAsJsonArray(Utils.identity);
                myFire = myMessage.extraInfo.getAsJsonArray(Utils.identity + "f");
            }
        }
    }

    // make move and fire action according to the action array getting from bucket manager
    private void action(){
        for (JsonElement i : myMovement) {
            myTank.move(i.getAsInt());
        }

        for (JsonElement i : enemyMovement) {
            enemyTank.move(i.getAsInt());
        }

        for (JsonElement i : myFire) {
            myTank.fire();
        }

        for (JsonElement i : enemyFire) {
            enemyTank.fire();
        }

        // limit the tank in the range of the screen
        enemyTank.inBound();
        myTank.inBound();
    }

    // send game over message to the other player
    private void sendGameOverMessage(boolean win){
        GameOverMessage msg = new GameOverMessage();
        msg.win = win;
    }

    // this class also register on the bus for game over message
    @Subscribe
    public void endGame(final GameOverMessage msg){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (msg.win == true) {
                    Utils.winOrLose = "win";
                } else {
                    Utils.winOrLose = "lose";
                }
                game.setScreen(new EndScreen(game));
            }
        });
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

    // disable all the resources used in this game
    @Override
    public void hide() {
        wallImage.dispose();
        tankImage.dispose();
        batch.dispose();
    }

    @Override
    public void dispose() {

    }

}
