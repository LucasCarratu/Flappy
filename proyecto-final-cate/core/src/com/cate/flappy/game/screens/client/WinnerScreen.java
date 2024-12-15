package com.cate.flappy.game.screens.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cate.flappy.MainFlppyBird;
import com.cate.flappy.game.screens.MenuScreen;

import static com.cate.flappy.Assets.font25;

public class WinnerScreen implements Screen {
    private static final float VIRTUAL_WIDTH = 480;
    private static final float VIRTUAL_HEIGHT = 800;
    private final SpriteBatch batch;

    private int winner;
    private final MainFlppyBird game;
    private OrthographicCamera camera;
    private FitViewport viewport;

    public WinnerScreen(int winner, MainFlppyBird game) {
        this.winner = winner;
        this.game = game;
        this.batch = new SpriteBatch();
    }

    @Override
    public void show() {

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();

        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        String winnerText = "Jugador " + (winner + 1) + " Gana!";
        if (winner == 3) winnerText = "Empate!";
        font25.draw(batch, winnerText, VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f,
                0, Align.center, false);
        font25.draw(batch, "Toca cualquier tecla\n para volver al menu",
                VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f - 50,
                0, Align.center, false);
        batch.end();

        if (Gdx.input.isKeyPressed(-1)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
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
        batch.dispose();
        font25.dispose();
    }
}
