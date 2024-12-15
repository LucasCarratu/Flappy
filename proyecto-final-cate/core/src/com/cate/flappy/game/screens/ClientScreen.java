package com.cate.flappy.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cate.flappy.Assets;
import com.cate.flappy.Counter.Pipe;
import com.cate.flappy.MainFlppyBird;
import com.cate.flappy.game.screens.client.WinnerScreen;
import com.cate.flappy.net.ClientBird;
import com.cate.flappy.net.RedUtiles;
import com.cate.flappy.screens.Screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ClientScreen extends Screens {
    public static final float SCREEN_WIDTH = 480;
    public static final float SCREEN_HEIGHT = 800;
    public static final float WORLD_WIDTH = 4.8f;
    public static final float WORLD_HEIGHT = 8;

    private SpriteBatch batch;
    private TextureRegion backgroundImage;
    private OrthographicCamera camera;
    private Viewport viewport;


    private BitmapFont font;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    public static ArrayList<ClientBird> birds = new ArrayList<>(2);

    public static HashMap<UUID, Pipe> pipes = new HashMap();

    public ClientScreen(MainFlppyBird game) {
        super(game);
        pipes = new HashMap();
        birds = new ArrayList<>(2);
        RedUtiles.empezarCliente();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        font = new BitmapFont();
        font.getData().setScale(0.1f);
        batch = new SpriteBatch();
        backgroundImage = Assets.background;
        for (int i = 0; i < 2; i++) {
            ClientBird bird = new ClientBird(Assets.bird, 2 + i, 2 + i);
            birds.add(bird);
        }


        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        uiViewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, uiCamera);
        uiViewport.apply();

    }

    @Override
    public void draw(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float bgWidth = WORLD_WIDTH;
        float bgHeight = WORLD_HEIGHT;

        batch.draw(backgroundImage, 0, 0, bgWidth, bgHeight);


        batch.draw(backgroundImage, (camera.position.x - bgWidth) - (camera.position.x % bgWidth), 0, bgWidth, bgHeight);
        batch.draw(backgroundImage, camera.position.x - (camera.position.x % bgWidth), 0, bgWidth, bgHeight);
        batch.draw(backgroundImage, (camera.position.x + bgWidth) - (camera.position.x % bgWidth), 0, bgWidth, bgHeight);


        for (Pipe pipe : pipes.values()) {
            pipe.draw(batch);
        }


        for (int i = 0; i < birds.size(); i++) {
            System.out.println("MY CLIENT ID "+ RedUtiles.idCliente);
            ClientBird bird = birds.get(i);
            if (bird.dead) continue;
            if (i == RedUtiles.idCliente) bird.setAlpha(1f);
            else bird.setAlpha(0.4f);
            bird.draw(batch);
        }

        batch.end();


        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);


        batch.begin();

        for (int i = 0; i < 2; i++) {
            Assets.font.draw(batch, "P" + (i + 1) + ": " + birds.get(i).score, camera.position.x, viewport.getScreenHeight() / (1.07f + (i * 0.15f)));
        }


        batch.end();
    }

    @Override
    public void update(float delta) {
        if (RedUtiles.ganador != -1) {
            game.setScreen(new WinnerScreen(RedUtiles.ganador, game));
        }

        for (int i = 0; i < birds.size(); i++) {
            ClientBird bird = birds.get(i);


            if (i == RedUtiles.idCliente) camera.position.set(bird.getX() + 1.2f, camera.position.y, 0);

            bird.update(delta);
        }

        if (Gdx.input.justTouched()) {
            RedUtiles.cliente.enviarMensaje("volar");
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
    }

    public static void addPipe(float x, float y, int type, UUID id) {
        pipes.put(id, new Pipe(x, y, type, id));
    }

    public static void updatePipe(UUID id, float x, float y) {
        pipes.get(id).position.x = x;
        pipes.get(id).position.y = y;
    }
}
