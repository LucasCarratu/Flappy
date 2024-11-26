package com.cate.flappy.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cate.flappy.Assets;
import com.cate.flappy.MainFlppyBird;
import com.cate.flappy.game.screens.server.ServerScreen;
import com.cate.flappy.game.screens.singleplayer.GameScreen;
import com.cate.flappy.screens.Screens;

public class MenuScreen extends Screens {

    private final BitmapFont font;
    private final Viewport viewport;

    public MenuScreen(MainFlppyBird game) {
        super(game);


        viewport = new FitViewport(480, 800);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        font = Assets.font;
        font.getData().setScale(0.5f);


        createButton("Un Jugador", 240, 600, 0);
        createButton("Multijugador", 240, 500, 1);
        createButton("Servidor", 240, 400, 2);
        createButton("Salir", 240, 300, 3);
    }

    private void createButton(String text, float x, float y, final int optionIndex) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        TextButton button = new TextButton(text, buttonStyle);
        button.setPosition(x - 100, y - 25);
        button.setSize(200, 50);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (optionIndex) {
                    case 0:
                        System.out.println("Un Jugador seleccionado");

                        game.setScreen(new GameScreen(game));
                        break;
                    case 1:
                        System.out.println("Multijugador seleccionado");

                        game.setScreen(new ClientScreen(game));
                        break;
                    case 2:
                        System.out.println("Servidor seleccionado");

                        game.setScreen(new ServerScreen(game));
                        break;
                    case 3:
                        Gdx.app.exit();
                        break;
                }
            }
        });

        stage.addActor(button);
    }

    @Override
    public void draw(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);


        stage.act(delta);
        stage.draw();


        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        drawCenteredText("Menu Principal", 700);
        spriteBatch.end();
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        stage.dispose();
    }

    public void drawCenteredText(String text, float y) {
        font.draw(spriteBatch, text, 0, y, 480, Align.center, true);
    }

}
