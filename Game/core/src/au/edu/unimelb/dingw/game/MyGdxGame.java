package au.edu.unimelb.dingw.game;

import com.badlogic.gdx.Gdx;


import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class MyGdxGame extends ApplicationAdapter {
    private Texture bulletImage;
    private Texture tankImage;
    private Texture wallImage;
    private int mDirection;
    //    private Sound dropSound;
//    private Music rainMusic;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle tank;
//    private Sprite tank;
    private Array<Bullet> bullets;
    private long lastDropTime;
    private Array<Rectangle> walls;


    @Override
    public void create() {
        // load the images for the droplet and the tank, 64x64 pixels each
        wallImage = new Texture(Gdx.files.internal("commonWall.gif"));
        tankImage = new Texture(Gdx.files.internal("tankR.gif"));
        mDirection = 2;

        // load the drop sound effect and the rain background "music"
//        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
//        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // start the playback of the background music immediately
////        rainMusic.setLooping(true);
//        rainMusic.play();

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 768);
        batch = new SpriteBatch();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        // create a Rectangle to logically represent the tank
        tank = new Rectangle();
//        tank = new Sprite(tankImage);
//        tank.setPosition(w/2 - tank.getWidth()/2, h/2 - tank.getHeight()/2);
        tank.x = 1024 / 2 - 40 / 2; // center the tank horizontally
        tank.y = 20; // bottom left corner of the tank is 20 pixels above the bottom screen edge
        tank.width = 40;
        tank.height = 40;

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
    public void render() {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the tank and
        // all drops
        batch.begin();
        batch.draw(tankImage, tank.x, tank.y);

//        tank.draw(batch);
        for (Bullet bullet : bullets) {
            batch.draw(bullet.getBulletImage(), bullet.getBullet().x, bullet.getBullet().y);
        }
        for (Rectangle wall: walls) {
            batch.draw(wallImage, wall.x, wall.y);
        }
        batch.end();

        // process user input
//        if (Gdx.input.isTouched()) {
//            Vector3 touchPos = new Vector3();
//            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//            camera.unproject(touchPos);
//            tank.x = touchPos.x - 64 / 2;
//        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {

            if (Gdx.input.isKeyPressed(Keys.UP)){
                move(5);
                System.out.println(5);
            }else if (Gdx.input.isKeyPressed(Keys.DOWN)){
                move(7);
                System.out.println(7);
            }else {
                move(1);
                System.out.println(1);
            }
//            move(1);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            if (Gdx.input.isKeyPressed(Keys.UP)){
                move(6);
            }else if (Gdx.input.isKeyPressed(Keys.DOWN)){
                move(8);
            }else {
                move(2);
            }
//            move(2);
        }
        if (Gdx.input.isKeyPressed(Keys.UP)){
            if (Gdx.input.isKeyPressed(Keys.LEFT)){
                move(5);
                System.out.println(5);
            }else if (Gdx.input.isKeyPressed(Keys.RIGHT)){
                move(6);
                System.out.println(6);
            }else {
                move(3);
                System.out.println(3);
            }

        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)){
                move(7);
                System.out.println(7);
            }else if (Gdx.input.isKeyPressed(Keys.RIGHT)){
                move(8);
                System.out.println(8);
            }else {
                move(4);
                System.out.println(4);
            }
        }
//        if (Gdx.input.isKeyPressed(Keys.LEFT) && Gdx.input.isKeyPressed(Keys.UP)) move(5);
//        if (Gdx.input.isKeyPressed(Keys.RIGHT) && Gdx.input.isKeyPressed(Keys.UP)) move(6);
//        if (Gdx.input.isKeyPressed(Keys.LEFT) && Gdx.input.isKeyPressed(Keys.DOWN)) move(7);
//        if (Gdx.input.isKeyPressed(Keys.RIGHT) && Gdx.input.isKeyPressed(Keys.DOWN)) move(8);
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) fire();

        // make sure the tank stays within the screen bounds
        if (tank.x < 0) tank.x = 0;
        if (tank.x > 1024 - 40) tank.x = 1024 - 40;
        if (tank.y < 0) tank.y = 0;
        if (tank.y > 768 - 40) tank.y = 768 - 40;
//        if (tank.getX() < 0) tank.setX(0);
//        if (tank.getX() > 1024 - tank.getWidth()) tank.setX(1024 - tank.getWidth());
//        if (tank.getY() < 0) tank.setY(0);
//        if (tank.getY() > 768 - tank.getHeight()) tank.setX(768 - tank.getHeight());

        // check if we need to create a new raindrop
//        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the tank. In the later case we play back
        // a sound effect as well.
        Iterator<Bullet> iter = bullets.iterator();
        Iterator<Rectangle> iter2 = walls.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.changeBulletPosition();
            if (bullet.outOfScreen()) iter.remove();
            while (iter2.hasNext()){
                Rectangle wall = iter2.next();
                if (bullet.getBullet().overlaps(wall)) {
                    iter.remove();
                    iter2.remove();
                }
            }
        }
        while (iter2.hasNext()){
            Rectangle wall = iter2.next();
            if (wall.overlaps(tank)) {
                restrictTank(wall);
            }
        }
    }

    public void restrictTank(Rectangle wall) {

//        final float nowX = tank.x;
//        final float nowY = tank.y;
        switch (mDirection) {
            case 1:
//                tank.x = wall.x + 22;
                tank.setX(wall.x + wall.getWidth());
                break;
            case 2:
//                tank.x = wall.x - 40;
                tank.setX(wall.x - tank.getWidth());
                break;
            case 3:
//                tank.y = wall.y - 40;
                tank.setY(wall.y - tank.getHeight());
                break;
            case 4:
//                tank.y = wall.y + 21;
                tank.setY(wall.y + wall.getHeight());
                break;

            case 5:
//                tank.x = wall.x + 22;
//                tank.y = wall.y - 43;
                tank.setX(wall.x + wall.getWidth());
                tank.setY(wall.y - tank.getHeight());
//                tank.x = nowX;
//                tank.y = nowY;
                break;
            case 6:
//                tank.x = wall.x - 43;
//                tank.y = wall.y - 43;
                tank.setX(wall.x + tank.getWidth());
                tank.setY(wall.y - tank.getHeight());
//                tank.x = nowX;
//                tank.y = nowY;
                break;
            case 7:
//                tank.x = wall.x + 22;
//                tank.y = wall.y + 21;
                tank.setX(wall.x + wall.getWidth());
                tank.setY(wall.y - wall.getHeight());
//                tank.x = nowX;
//                tank.y = nowY;
                break;
            case 8:
//                tank.x = wall.x - 43;
//                tank.y = wall.y + 21;
                tank.setX(wall.x + tank.getWidth());
                tank.setY(wall.y - wall.getHeight());
//                tank.x = nowX;
//                tank.y = nowY;
                break;
            default:
                break;
        }
    }

    public void move(int direction) {
        switch (direction) {
            case 1:
                mDirection = 1;
                tankImage = new Texture(Gdx.files.internal("tankL.gif"));
//                tank.setTexture(tankImage);
                System.out.println(mDirection);
                tank.x -= 100 * Gdx.graphics.getDeltaTime();
//                tank.translateX(-200*Gdx.graphics.getDeltaTime());
                break;
            case 2:
                mDirection = 2;
                tankImage = new Texture(Gdx.files.internal("tankR.gif"));
//                tank.setTexture(tankImage);
                tank.x += 100 * Gdx.graphics.getDeltaTime();
                System.out.println(mDirection);
//                tank.translateX(200*Gdx.graphics.getDeltaTime());
                break;
            case 3:
                mDirection = 3;
                tankImage = new Texture(Gdx.files.internal("tankU.gif"));
//                tank.setTexture(tankImage);
                tank.y += 100 * Gdx.graphics.getDeltaTime();
                System.out.println(mDirection);
//                tank.translateY(200*Gdx.graphics.getDeltaTime());
                break;
            case 4:
                mDirection = 4;
                tankImage = new Texture(Gdx.files.internal("tankD.gif"));
//                tank.setTexture(tankImage);
                tank.y -= 100 * Gdx.graphics.getDeltaTime();
                System.out.println(mDirection);
//                tank.translateY(-200*Gdx.graphics.getDeltaTime());
                break;
            case 5:
                mDirection = 5;
                tankImage = new Texture(Gdx.files.internal("tankLU.gif"));
//                tank.setTexture(tankImage);
                System.out.println(mDirection);
                tank.y += 35 * Gdx.graphics.getDeltaTime();
                tank.x -= 35 * Gdx.graphics.getDeltaTime();
//                tank.translate(-141*Gdx.graphics.getDeltaTime(),141*Gdx.graphics.getDeltaTime());
                break;
            case 6:
                mDirection = 6;
                tankImage = new Texture(Gdx.files.internal("tankRU.gif"));
//                tank.setTexture(tankImage);
                tank.y += 35 * Gdx.graphics.getDeltaTime();
                tank.x += 35 * Gdx.graphics.getDeltaTime();
                System.out.println(mDirection);
//                tank.translate(141*Gdx.graphics.getDeltaTime(),141*Gdx.graphics.getDeltaTime());
                break;
            case 7:
                mDirection = 7;
                tankImage = new Texture(Gdx.files.internal("tankLD.gif"));
//                tank.setTexture(tankImage);
                tank.y -= 35 * Gdx.graphics.getDeltaTime();
                tank.x -= 35 * Gdx.graphics.getDeltaTime();
                System.out.println(mDirection);
//                tank.translate(-141*Gdx.graphics.getDeltaTime(),-141*Gdx.graphics.getDeltaTime());
                break;
            case 8:
                mDirection = 8;
                tankImage = new Texture(Gdx.files.internal("tankRD.gif"));
//                tank.setTexture(tankImage);
                tank.y -= 35 * Gdx.graphics.getDeltaTime();
                tank.x += 35 * Gdx.graphics.getDeltaTime();
                System.out.println(mDirection);
//                tank.translate(141*Gdx.graphics.getDeltaTime(),-141*Gdx.graphics.getDeltaTime());
                break;
            default:
                break;
        }
    }

    public void fire() {

        switch (mDirection) {
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

        Bullet bullet = new Bullet(bulletImage, mDirection, tank.getX(), tank.getY());
        bullets.add(bullet);
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        wallImage.dispose();
        bulletImage.dispose();
        tankImage.dispose();
        batch.dispose();
    }
}
