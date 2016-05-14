package au.edu.unimelb.dingw.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by dingwang on 16/5/4.
 */
public class Tank {
    private Texture tankImage;
    private int direction;
    private Rectangle tank;

    public Tank(Texture tankImage, int direction,float x, float y){
        this.direction = direction;
        this.tankImage = tankImage;
        createTank(x, y);
    }

    private void createTank(float x, float y) {
        this.tank = new Rectangle();
        this.tank.width = 40;
        this.tank.height = 40;
        this.tank.x = x;
        this.tank.y = y;

    }

    public void setTankImage(Texture tankImage) {
        this.tankImage = tankImage;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setTank(Rectangle tank) {
        this.tank = tank;
    }

    public Texture getTankImage() {
        return tankImage;
    }

    public int getDirection() {
        return direction;
    }

    public Rectangle getTank() {
        return tank;
    }

    public void updateTankState(float x, float y, int direction){
        this.tank.x = x;
        this.tank.y = y;
        this.direction = direction;
        switch (direction){
            case 1:
                tankImage = new Texture(Gdx.files.internal("tankL.gif"));
                break;
            case 2:
                tankImage = new Texture(Gdx.files.internal("tankR.gif"));
                break;
            case 3:
                tankImage = new Texture(Gdx.files.internal("tankU.gif"));
                break;
            case 4:
                tankImage = new Texture(Gdx.files.internal("tankD.gif"));
                break;
            case 5:
                tankImage = new Texture(Gdx.files.internal("tankLU.gif"));
                break;
            case 6:
                tankImage = new Texture(Gdx.files.internal("tankRU.gif"));
                break;
            case 7:
                tankImage = new Texture(Gdx.files.internal("tankLD.gif"));
                break;
            case 8:
                tankImage = new Texture(Gdx.files.internal("tankRD.gif"));
                break;
            default:
                break;
        }
    }

    public void move(int direction) {
            switch (direction) {
                case 1:
                    this.direction = 1;
                    tankImage = new Texture(Gdx.files.internal("tankL.gif"));
                    tank.x -= 100 * Gdx.graphics.getDeltaTime();
                    break;
                case 2:
                    this.direction = 2;
                    tankImage = new Texture(Gdx.files.internal("tankR.gif"));
                    tank.x += 100 * Gdx.graphics.getDeltaTime();
                    break;
                case 3:
                    this.direction = 3;
                    tankImage = new Texture(Gdx.files.internal("tankU.gif"));
                    tank.y += 100 * Gdx.graphics.getDeltaTime();
                    break;
                case 4:
                    this.direction = 4;
                    tankImage = new Texture(Gdx.files.internal("tankD.gif"));
                    tank.y -= 100 * Gdx.graphics.getDeltaTime();
                    break;
                case 5:
                    this.direction = 5;
                    tankImage = new Texture(Gdx.files.internal("tankLU.gif"));
                    tank.y += 35 * Gdx.graphics.getDeltaTime();
                    tank.x -= 35 * Gdx.graphics.getDeltaTime();
                    break;
                case 6:
                    this.direction = 6;
                    tankImage = new Texture(Gdx.files.internal("tankRU.gif"));
                    tank.y += 35 * Gdx.graphics.getDeltaTime();
                    tank.x += 35 * Gdx.graphics.getDeltaTime();
                    break;
                case 7:
                    this.direction = 7;
                    tankImage = new Texture(Gdx.files.internal("tankLD.gif"));
                    tank.y -= 35 * Gdx.graphics.getDeltaTime();
                    tank.x -= 35 * Gdx.graphics.getDeltaTime();
                    break;
                case 8:
                    this.direction = 8;
                    tankImage = new Texture(Gdx.files.internal("tankRD.gif"));
                    tank.y -= 35 * Gdx.graphics.getDeltaTime();
                    tank.x += 35 * Gdx.graphics.getDeltaTime();
                    break;
                default:
                    break;
            }
    }
    public void restrictTank() {

        switch (this.direction) {
            case 1:
                tank.x += 100 * Gdx.graphics.getDeltaTime();
                break;
            case 2:
                tank.x -= 100 * Gdx.graphics.getDeltaTime();
                break;
            case 3:
                tank.y -= 100 * Gdx.graphics.getDeltaTime();
                break;
            case 4:
                tank.y += 100 * Gdx.graphics.getDeltaTime();
                break;
            case 5:
                tank.y -= 35 * Gdx.graphics.getDeltaTime();
                tank.x += 35 * Gdx.graphics.getDeltaTime();
                break;
            case 6:
                tank.y -= 35 * Gdx.graphics.getDeltaTime();
                tank.x -= 35 * Gdx.graphics.getDeltaTime();
                break;
            case 7:
                tank.y += 35 * Gdx.graphics.getDeltaTime();
                tank.x += 35 * Gdx.graphics.getDeltaTime();
                break;
            case 8:
                tank.y += 35 * Gdx.graphics.getDeltaTime();
                tank.x -= 35 * Gdx.graphics.getDeltaTime();
                break;
            default:
                break;
        }
    }

    public void inBound(){
        if (tank.x < 0) tank.x = 0;
        if (tank.x > 1024 - 40) tank.x = 1024 - 40;
        if (tank.y < 0) tank.y = 0;
        if (tank.y > 768 - 40) tank.y = 768 - 40;
    }

    public void fire() {

        Texture bulletImage = new Texture(Gdx.files.internal("bulletL.gif"));
        switch (this.direction) {
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
        Bullet bullet = new Bullet(bulletImage, direction, tank.x, tank.y);
        Utils.bus.post(bullet);

    }

}
