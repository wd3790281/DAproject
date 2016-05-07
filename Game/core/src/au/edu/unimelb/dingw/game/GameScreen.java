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

import java.util.Iterator;

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
    private Tank tank;
    //    private Sprite tank;
    private Array<Bullet> bullets;
    private long lastDropTime;
    private Array<Rectangle> walls;

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
////        rainMusic.setLooping(true);
//        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 768);
        batch = new SpriteBatch();

        // create a Rectangle to logically represent the tank
        tank = new Tank(tankImage, mDirection, 492, 20);

        bullets = new Array<Bullet>();

        walls = new Array<Rectangle>();
//        for (float x = (float) 100.0; x < 1024; x += 22) {
        for (float y = 0; y < 768; y += 21) {
            createWalls(100, y);
        }
        for (float y = 0; y < 768; y += 21) {
            createWalls(y, 100);
        }
//        }
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
        batch.draw(tank.getTankImage(), tank.getTank().getX(), tank.getTank().getY());

        for (Bullet bullet : bullets) {
            batch.draw(bullet.getBulletImage(), bullet.getBullet().x, bullet.getBullet().y);
        }
        for (Rectangle wall: walls) {
            batch.draw(wallImage, wall.x, wall.y);
        }
        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            if (Gdx.input.isKeyPressed(Input.Keys.UP)){
                tank.move(5);
            }else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                tank.move(7);
            }else {
                tank.move(1);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)){
                tank.move(6);
            }else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                tank.move(8);
            }else {
                tank.move(2);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                tank.move(5);
            }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                tank.move(6);
            }else {
                tank.move(3);
            }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                tank.move(7);
            }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                tank.move(8);
            }else {
                tank.move(4);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) fire();

        // make sure the tank stays within the screen bounds
        tank.inBound();


        Iterator<Bullet> iter = bullets.iterator();
        Iterator<Rectangle> iter2 = walls.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.changeBulletPosition();
            if (bullet.outOfScreen()) iter.remove();
            while (iter2.hasNext()){
                Rectangle wall = iter2.next();
                if (bullet.getBullet().overlaps(wall) || wall.overlaps(bullet.getBullet()) || wall.contains(bullet.getBullet())) {
                    iter.remove();
                    iter2.remove();
                }
            }
        }
        while (iter2.hasNext()){
            Rectangle wall = iter2.next();
            if (wall.overlaps(tank.getTank()) || tank.getTank().overlaps(wall)) {
                tank.restrictTank(wall);
            }
        }

    }

    public void fire() {

        switch (tank.getDirection()) {
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

        Bullet bullet = new Bullet(bulletImage, tank.getDirection(), tank.getTank().getX(), tank.getTank().getY());
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
