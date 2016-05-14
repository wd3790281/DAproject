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
import com.badlogic.gdx.utils.IntArray;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Interner;
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

    private Texture bulletImage;
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
    private Array<Integer> enemyMovement;
    private Array<Integer> myFireAction;

    public GameScreen(Game game){
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
            myTank = new Tank(enemyTankImage, 1, 492, 700 );
            enemyTank = new Tank(tankImage, mDirection, 492, 20);
        }

        bullets = new Array<Bullet>();

        myPressedKeys = new Array<Integer>();
        myFireAction = new Array<Integer>();

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

        batch.begin();
        batch.draw(myTank.getTankImage(), myTank.getTank().getX(), myTank.getTank().getY());
        batch.draw(enemyTank.getTankImage(), enemyTank.getTank().getX(), enemyTank.getTank().getY());
        for (Bullet bullet : bullets) {
            batch.draw(bullet.getBulletImage(), bullet.getBullet().x, bullet.getBullet().y);
        }

        for (Rectangle wall: walls) {
            batch.draw(wallImage, wall.x, wall.y);
        }
        batch.end();

        myPressedKeys.clear();
        myFireAction.clear();

        exchangeMessageHashMap = BucketManager.defaultManager.getMessages();
        if (exchangeMessageHashMap != null) {
            enemyMessage = exchangeMessageHashMap.get(1);

            if (enemyMessage != null) {
                JsonArray moveArray;
                if (Utils.identity.equals("host")) {
                    moveArray = enemyMessage.extraInfo.getAsJsonArray("client");
                } else {
                    moveArray = enemyMessage.extraInfo.getAsJsonArray("host");
                }
                System.out.println("game" + " " + enemyMessage.extraInfo.toString());

                for (JsonElement i : moveArray) {
                    enemyTank.move(i.getAsInt());
                }

                JsonArray fireArray;
                if (Utils.identity.equals("host")) {
                    fireArray = enemyMessage.extraInfo.getAsJsonArray("clientf");
                } else {
                    fireArray = enemyMessage.extraInfo.getAsJsonArray("hostf");
                }
                for (JsonElement i : fireArray) {
                    enemyTank.fire();
                }
            }
        }

        enemyTank.inBound();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            if (Gdx.input.isKeyPressed(Input.Keys.UP)){
                myTank.move(5);
                myPressedKeys.add(5);
            }else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                myTank.move(7);
                myPressedKeys.add(7);
            }else {
                myTank.move(1);
                myPressedKeys.add(1);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)){
                myTank.move(6);
                myPressedKeys.add(6);
            }else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                myTank.move(8);
                myPressedKeys.add(8);
            }else {
                myTank.move(2);
                myPressedKeys.add(2);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                myTank.move(5);
                myPressedKeys.add(5);
            }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                myTank.move(6);
                myPressedKeys.add(6);
            }else {
                myTank.move(3);
                myPressedKeys.add(3);
            }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                myTank.move(7);
                myPressedKeys.add(7);
            }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                myTank.move(8);
                myPressedKeys.add(8);
            }else {
                myTank.move(4);
                myPressedKeys.add(4);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            myTank.fire();
            myFireAction.add(1);
        }

        // make sure the myTank stays within the screen bounds
        myTank.inBound();


        for (int i = 0; i < bullets.size; i ++) {

            Bullet bullet = bullets.get(i);

            bullet.changeBulletPosition();
            if (bullet.outOfScreen()) bullets.removeIndex(i);
            if (bullet.getBullet().overlaps(myTank.getTank())) {
                bullets.removeIndex(i);

            } else if (bullet.getBullet().overlaps(enemyTank.getTank())){
                bullets.removeIndex(i);
            }
            for (int j = 0; j < walls.size; j ++){
                Rectangle wall = walls.get(j);
                if (bullet.getBullet().overlaps(wall) || wall.overlaps(bullet.getBullet()) || wall.contains(bullet.getBullet())) {
                    bullets.removeIndex(i);
                    walls.removeIndex(j);
                }

            }
        }
        for (int j = 0; j < walls.size; j ++){
            Rectangle wall = walls.get(j);
            if (wall.overlaps(myTank.getTank()) || myTank.getTank().overlaps(wall)) {
                myTank.restrictTank();
            }
        }
        sendMessage();

    }

//    public void fire() {
//
//        switch (myTank.getDirection()) {
//            case 1:
//                bulletImage = new Texture(Gdx.files.internal("bulletL.gif"));
//                break;
//            case 2:
//                bulletImage = new Texture(Gdx.files.internal("bulletR.gif"));
//                break;
//            case 3:
//                bulletImage = new Texture(Gdx.files.internal("bulletU.gif"));
//                break;
//            case 4:
//                bulletImage = new Texture(Gdx.files.internal("bulletD.gif"));
//                break;
//            case 5:
//                bulletImage = new Texture(Gdx.files.internal("bulletLU.gif"));
//                break;
//            case 6:
//                bulletImage = new Texture(Gdx.files.internal("bulletRU.gif"));
//                break;
//            case 7:
//                bulletImage = new Texture(Gdx.files.internal("bulletLD.gif"));
//                break;
//            case 8:
//                bulletImage = new Texture(Gdx.files.internal("bulletRD.gif"));
//        }
//
//        Bullet bullet = new Bullet(bulletImage, myTank.getDirection(), myTank.getTank().getX(), myTank.getTank().getY());
//        bullets.add(bullet);
//    }
    @Subscribe
    private void addBullet(Bullet bullet){
        bullets.add(bullet);
    }

    private void sendMessage(){
        JsonObject jsonObject = new JsonObject();
        JsonArray myMove = new JsonArray();
        for (Integer i : myPressedKeys){
            myMove.add(i);
//            System.out.println(i);
        }
        jsonObject.add(Utils.identity, myMove);

        JsonArray fire = new JsonArray();

        for (Integer j : myFireAction){
            fire.add(j);
        }
        jsonObject.add(Utils.identity + "f", fire);

        message.extraInfo = jsonObject;
        if (Utils.identity.equals("host")){
            Utils.host.send(message);
        } else {
            Utils.client.send(message);
        }
//        System.out.println(message.extraInfo.toString());

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
        bulletImage.dispose();
        tankImage.dispose();
        batch.dispose();
    }

    @Override
    public void dispose() {

    }

}
