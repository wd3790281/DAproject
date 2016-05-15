package au.edu.unimelb.dingw.game;

import au.edu.unimelb.algorithms.BucketManager;
import au.edu.unimelb.messages.GameStateExchangeMessage;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
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
public class GameScreen implements Screen {

    private Game game;

    private Texture tankImage;
    private Texture wallImage;
    private int mDirection;
    //    private Sound dropSound;
//    private Music rainMusic;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Tank myTank;
    private Tank enemyTank;
    private Array<Bullet> bullets;
    private Array<Rectangle> walls;
    private Array<Integer> myPressedKeys;
    private GameStateExchangeMessage message;
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
        wallImage = new Texture(Gdx.files.internal("commonWall.gif"));
        tankImage = new Texture(Gdx.files.internal("tankR.gif"));
        Texture enemyTankImage = new Texture(Gdx.files.internal("tankL.gif"));
        mDirection = 2;
        message = new GameStateExchangeMessage();

        Utils.bus.register(this);

        // load the drop sound effect and the rain background "music"
//        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
//        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // start the playback of the background music immediately
//        rainMusic.setLooping(true);
//        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 768);
        batch = new SpriteBatch();

        // create a Rectangle to logically represent the myTank
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

        walls = new Array<Rectangle>();
        for (float y = 0; y < 768; y += 21) {
            createWalls(100, y);
        }
        for (float y = 0; y < 768; y += 21) {
            createWalls(y, 100);
        }
    }

    private void createWalls(float x, float y) {
        Rectangle wall = new Rectangle();
        wall.x = x;
        wall.y = y;
        wall.width = 22;
        wall.height = 21;
        walls.add(wall);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);
        updateGameState();

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

        action();

        myPressedKeys.clear();
        myFireAction.clear();

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


        for (int i = 0; i < bullets.size; i++) {

            Bullet bullet = bullets.get(i);

            bullet.changeBulletPosition();
            if (bullet.outOfScreen()) bullets.removeIndex(i);
            if (bullet.getBullet().overlaps(myTank.getTank())) {
                bullets.removeIndex(i);
                Utils.winOrLose = "lose";
                game.setScreen(new EndScreen(game));

            } else if (bullet.getBullet().overlaps(enemyTank.getTank())) {
                bullets.removeIndex(i);
                Utils.winOrLose = "win";
                game.setScreen(new EndScreen(game));
            }
            for (int j = 0; j < walls.size; j++) {
                Rectangle wall = walls.get(j);
                if (bullet.getBullet().overlaps(wall) || wall.overlaps(bullet.getBullet()) || wall.contains(bullet.getBullet())) {
                    bullets.removeIndex(i);
                    walls.removeIndex(j);
                }

            }
        }
        for (int j = 0; j < walls.size; j++) {
            Rectangle wall = walls.get(j);
            if (wall.overlaps(myTank.getTank()) || myTank.getTank().overlaps(wall)) {
                myTank.restrictTank();
            }
        }
        sendMessage();

    }

    @Subscribe
    private void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    private void sendMessage() {
        JsonObject jsonObject = new JsonObject();
        JsonArray myMove = new JsonArray();
        for (Integer i : myPressedKeys) {
            myMove.add(i);
//            System.out.println(i);
        }
        jsonObject.add(Utils.identity, myMove);

        JsonArray fire = new JsonArray();

        for (Integer j : myFireAction) {
            fire.add(j);
        }
        jsonObject.add(Utils.identity + "f", fire);

        jsonObject.addProperty(Utils.identity + "x", myTank.getTank().x);
        jsonObject.addProperty(Utils.identity + "y", myTank.getTank().y);
        jsonObject.addProperty(Utils.identity + "d", myTank.getDirection());
        message.extraInfo = jsonObject;
        if (Utils.identity.equals("host")) {
            Utils.host.send(message);
        } else {
            Utils.client.send(message);
        }
//        System.out.println(message.extraInfo.toString());

        BucketManager.defaultManager.receiveMessage(message, 0);
    }

    private void updateGameState(){
        exchangeMessageHashMap = BucketManager.defaultManager.getMessages();
        if (exchangeMessageHashMap != null) {
            enemyMessage = exchangeMessageHashMap.get(1);

            if (enemyMessage != null) {
//                System.out.println(enemyMessage.extraInfo.toString());
                if (Utils.identity.equals("host")) {
//                    System.out.println(enemyMessage.extraInfo);
                    if (enemyMessage.extraInfo.get("clientx") != null
                            && enemyMessage.extraInfo.get("clienty") != null
                            && enemyMessage.extraInfo.get("clientd") != null ) {
//                        System.out.println(enemyMessage.extraInfo.get("clientx").getAsFloat());
//                        System.out.println(enemyMessage.extraInfo.get("clienty").getAsFloat());
//                        System.out.println(enemyMessage.extraInfo.get("clientd").getAsInt());
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

            myMessage = exchangeMessageHashMap.get(0);

            if (myMessage != null) {
//                System.out.println(myMessage.extraInfo.toString());
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
        enemyTank.inBound();
        myTank.inBound();
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
        wallImage.dispose();
        tankImage.dispose();
        batch.dispose();
    }

    @Override
    public void dispose() {

    }

}
