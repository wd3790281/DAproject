package au.edu.unimelb.dingw.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;


import com.badlogic.gdx.assets.AssetManager;

public class MyGdxGame extends Game {

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // Load assets
//        Assets.instance.init(new AssetManager());
        // Start game at menu screen
        setScreen(new MenuScreen(this));
    }
}
