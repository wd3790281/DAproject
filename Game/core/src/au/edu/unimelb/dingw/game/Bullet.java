package au.edu.unimelb.dingw.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by dingwang on 16/4/28.
 */

// this class is bullet class, it has message to create the bullet and make the bullet move
public class Bullet {

    private Texture bulletImage;
    private int direction;
    private Rectangle bullet;

    public Bullet(Texture bulletImage, int direction, float x, float y){
        this.bulletImage = bulletImage;
        this.direction = direction;
        createBullet(x, y);

    }

    // the x and y is the postion of the tank who fires, this method compute the
    // creation position of the bullet according to the tank
    private void createBullet(float x, float y) {
        this.bullet = new Rectangle();
        this.bullet.width = 2;
        this.bullet.height = 2;

        switch (this.direction){
            case 1:
                this.bullet.x = x - 5;
                this.bullet.y = y + 15;
                break;
            case 2:
                this.bullet.x = x + 45;
                this.bullet.y = y + 15;
                break;
            case 3:
                this.bullet.y = y + 45;
                this.bullet.x = x + 12;
                break;
            case 4:
                this.bullet.y = y - 5;
                this.bullet.x = x + 12;
                break;
            case 5:
                this.bullet.y = y + 31;
                this.bullet.x = x;
                break;
            case 6:
                this.bullet.y = y + 40;
                this.bullet.x = x + 40;
                break;
            case 7:
                this.bullet.y = y - 15;
                this.bullet.x = x - 15;
                break;
            case 8:
                this.bullet.y = y;
                this.bullet.x = x + 31;
                break;
        }
    }

    // make the bullet to move in delta time. The bullet move 200 pixels
    public void changeBulletPosition(){
        switch (direction){
            case 1:
                this.bullet.x -= 200 * Gdx.graphics.getDeltaTime();
                break;
            case 2:
                this.bullet.x += 200 * Gdx.graphics.getDeltaTime();
                break;
            case 3:
                this.bullet.y += 200 * Gdx.graphics.getDeltaTime();
                break;
            case 4:
                this.bullet.y -= 200 * Gdx.graphics.getDeltaTime();
                break;
            case 5:
                this.bullet.y += 141 * Gdx.graphics.getDeltaTime();
                this.bullet.x -= 141 * Gdx.graphics.getDeltaTime();
                break;
            case 6:
                this.bullet.y += 141 * Gdx.graphics.getDeltaTime();
                this.bullet.x += 141 * Gdx.graphics.getDeltaTime();
                break;
            case 7:
                this.bullet.y -= 141 * Gdx.graphics.getDeltaTime();
                this.bullet.x -= 141 * Gdx.graphics.getDeltaTime();
                break;
            case 8:
                this.bullet.y -= 141 * Gdx.graphics.getDeltaTime();
                this.bullet.x += 141 * Gdx.graphics.getDeltaTime();
                break;
        }
    }

    // check whether the bullet is out of the screen
    public Boolean outOfScreen() {
        if ((bullet.x + 2) < 0 || bullet.x > 1024 || (bullet.y + 2) < 0 || bullet.y > 768)
            return true;
        return false;
    }

    public Texture getBulletImage() {
        return bulletImage;
    }

    public int getDirection() {
        return direction;
    }

    public Rectangle getBullet(){
        return this.bullet;
    }
}
