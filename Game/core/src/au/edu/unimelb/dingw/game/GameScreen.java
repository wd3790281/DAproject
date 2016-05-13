package au.edu.unimelb.dingw.game;

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

import java.util.ArrayList;

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
    private Array<Integer> pressedKeys;

    public GameScreen(Game game){
        this.game = game;
    }


    @Override
    public void show() {
        wallImage = new Texture(Gdx.files.internal("commonWall.gif"));
        tankImage = new Texture(Gdx.files.internal("tankR.gif"));
        mDirection = 2;

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
        myTank = new Tank(tankImage, mDirection, 492, 20);

        bullets = new Array<Bullet>();

        pressedKeys = new Array<Integer>();

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

        for (Bullet bullet : bullets) {
            batch.draw(bullet.getBulletImage(), bullet.getBullet().x, bullet.getBullet().y);
        }

        for (Rectangle wall: walls) {
            batch.draw(wallImage, wall.x, wall.y);
        }
        batch.end();

        pressedKeys.clear();


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            if (Gdx.input.isKeyPressed(Input.Keys.UP)){
                myTank.move(5);
                pressedKeys.add(5);
            }else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                myTank.move(7);
                pressedKeys.add(7);
            }else {
                myTank.move(1);
                pressedKeys.add(1);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)){
                myTank.move(6);
                pressedKeys.add(6);
            }else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                myTank.move(8);
                pressedKeys.add(8);
            }else {
                myTank.move(2);
                pressedKeys.add(2);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                myTank.move(5);
                pressedKeys.add(5);
            }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                myTank.move(6);
                pressedKeys.add(6);
            }else {
                myTank.move(3);
                pressedKeys.add(3);
            }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                myTank.move(7);
                pressedKeys.add(7);
            }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                myTank.move(8);
                pressedKeys.add(8);
            }else {
                myTank.move(4);
                pressedKeys.add(4);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) fire();

        // make sure the myTank stays within the screen bounds
        myTank.inBound();


        for (int i = 0; i < bullets.size; i ++) {

            Bullet bullet = bullets.get(i);

            bullet.changeBulletPosition();
            if (bullet.outOfScreen()) bullets.removeIndex(i);
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

    }

    public void fire() {

        switch (myTank.getDirection()) {
            case 1:
                bulletImage = new Texture(Gdx.files.internal("bulletL.gif"));
                break;
            case 2:
                bulletImage = new Texture(Gdx.files.internal("bulletR.gif"));
                break;
            case 3:
                bulletImage = new Texture(Gdx.files.internal("bulletU.gif"));
                break;
            case 4:
                bulletImage = new Texture(Gdx.files.internal("bulletD.gif"));
                break;
            case 5:
                bulletImage = new Texture(Gdx.files.internal("bulletLU.gif"));
                break;
            case 6:
                bulletImage = new Texture(Gdx.files.internal("bulletRU.gif"));
                break;
            case 7:
                bulletImage = new Texture(Gdx.files.internal("bulletLD.gif"));
                break;
            case 8:
                bulletImage = new Texture(Gdx.files.internal("bulletRD.gif"));
        }

        Bullet bullet = new Bullet(bulletImage, myTank.getDirection(), myTank.getTank().getX(), myTank.getTank().getY());
        bullets.add(bullet);
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
