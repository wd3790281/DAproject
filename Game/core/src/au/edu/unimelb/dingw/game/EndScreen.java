package au.edu.unimelb.dingw.game;

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

/**
 * Created by dingwang on 16/5/15.
 */
public class EndScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skinLibgdx;
    private Image imgBackground;

    public EndScreen (Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(1024, 768));
        Gdx.input.setInputProcessor(stage);
        rebuildStage();
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

    }

    private Table buildBackgroundLayer () {
        Table layer = new Table();
        layer.top();
        // + Background
        imgBackground = new Image(new Texture(Gdx.files.internal("end.png")));
        layer.add(imgBackground);
        return layer;
    }

    private void rebuildStage () {
        skinLibgdx = new Skin(Gdx.files.internal("uiskin.json"), new TextureAtlas("uiskin.atlas"));

        // build all layers
        Table layerBackground = buildBackgroundLayer();
        Table option = buildOptWinButtons();
        // assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(1024,768);
        stack.add(option);
        stack.add(layerBackground);

    }
    private Table buildOptWinButtons () {
        Table tbl = new Table();
        // + Separator
        Label label = new Label("You " + Utils.winOrLose + "!", skinLibgdx);
        label.setSize(200, 40);
        label.setPosition(412,100);
        tbl.addActor(label);
        // + Cancel Button with event handler

//        TextButton restartButton = new TextButton("Restart", skinLibgdx);
//        tbl.addActor(restartButton);
//        restartButton.setSize(300,50);
//        restartButton.setPosition(362, 20);
//        restartButton.setColor(0,0,0,1);
//        restartButton.addListener(new ChangeListener() {
//            @Override
//            public void changed (ChangeEvent event, Actor actor) {
//
//                game.setScreen(new MenuScreen(game));
//
//            }
//        });
        return tbl;
    }
}
