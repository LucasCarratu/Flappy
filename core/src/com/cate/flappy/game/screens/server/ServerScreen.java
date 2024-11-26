package com.cate.flappy.game.screens.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.cate.flappy.Assets;
import com.cate.flappy.MainFlppyBird;
import com.cate.flappy.net.RedUtiles;
import com.cate.flappy.screens.Screens;

public class ServerScreen extends Screens {
    static final int STATE_READY = 0;
    static final int STATE_RUNNING = 1;
    static final int STATE_GAME_OVER = 2;
    int state;

    ServerWorld oWorld;
    ServerRenderer renderer;

    Image getReady, tap, gameOver;

    public ServerScreen(MainFlppyBird game) {
        super(game);


        RedUtiles.empezarServidor();

        state = STATE_READY;

        oWorld = new ServerWorld(this);
        renderer = new ServerRenderer(spriteBatch, oWorld);

        getReady = new Image(Assets.getReady);

        tap = new Image(Assets.tap);
        tap.setPosition(SCREEN_WIDTH / 2 - tap.getWidth() / 2, 310);

        gameOver = new Image(Assets.gameOver);
        gameOver.setPosition(SCREEN_WIDTH / 2 - gameOver.getWidth() / 2, 400);

        stage.addActor(getReady);
        stage.addActor(tap);
    }

    @Override
    public void update(float delta) {
        if(RedUtiles.server.iniciaJuego) {
            updateRunning(delta);
        }
    }

    private void updateReady(float delta) {
        if ( Gdx.input.justTouched()) {
            getReady.addAction(Actions.fadeOut(.3f));
            tap.addAction(Actions.sequence(
                    Actions.fadeOut(.3f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            getReady.remove();
                            tap.remove();
                            state = STATE_RUNNING;
                        }
                    })
            ));
        }
    }

    private void updateRunning(float delta) {


        boolean jump = Gdx.input.justTouched();

        oWorld.update(delta, Gdx.input.isKeyPressed(Input.Keys.W), Gdx.input.isKeyPressed(Input.Keys.R));

        if (oWorld.state == ServerWorld.STATE_GAME_OVER) {
            state = STATE_GAME_OVER;
            stage.addActor(gameOver);
        }
    }

    private void updateGameOver(float delta) {
        if (Gdx.input.justTouched()) {
            gameOver.addAction(Actions.sequence(Actions.fadeOut(.3f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            gameOver.remove();
                            game.setScreen(new ServerScreen(game));
                        }
                    })
            ));
        }

    }

    @Override
    public void draw(float delta) {
        renderer.render(delta);

        oCam.update();
        spriteBatch.setProjectionMatrix(oCam.combined);

        spriteBatch.begin();
        String auxScore = ServerWorld.arrBirds.get(0).score + "";
        float width = Assets.getTextWidth(oWorld.score + auxScore);
        Assets.font.draw(spriteBatch, auxScore, SCREEN_WIDTH / 1.8f - width / 2f, 700);
        spriteBatch.end();
    }
}
