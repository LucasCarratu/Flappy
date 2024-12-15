package com.cate.flappy.game.screens.server;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.cate.flappy.Assets;
import com.cate.flappy.Counter.Bird;
import com.cate.flappy.Counter.Pipe;
import com.cate.flappy.screens.Screens;

public class ServerRenderer {
    final float WIDTH = Screens.WORLD_WIDTH;
    final float HEIGHT = Screens.WORLD_HEIGHT;

    SpriteBatch spriteBatch;
    ServerWorld oWorld;
    OrthographicCamera oCam;

    Box2DDebugRenderer renderBox;

    public ServerRenderer(SpriteBatch batcher, ServerWorld oWorld) {

        this.oCam = new OrthographicCamera(WIDTH, HEIGHT);
        this.oCam.position.set(WIDTH / 2f, HEIGHT / 2f, 0);
        this.spriteBatch = batcher;
        this.oWorld = oWorld;
        this.renderBox = new Box2DDebugRenderer();
    }
    public void render(float delta) {

        oCam.update();
        spriteBatch.setProjectionMatrix(oCam.combined);

        spriteBatch.begin();
        spriteBatch.disableBlending();
        drawBackground(delta);
        spriteBatch.enableBlending();
        drawPipes(delta);
        for(Bird bird : oWorld.arrBirds) {
            drawBird(bird, delta);
        }

        spriteBatch.end();


    }

    private void drawBird(Bird obj, float delta) {
        TextureRegion keyFrame;

        if (obj.state == Bird.STATE_NORMAL) {
            keyFrame = Assets.bird.getKeyFrame(obj.stateTime, true);
        } else {
            keyFrame = Assets.bird.getKeyFrame(obj.stateTime, false);
        }
        spriteBatch.draw(keyFrame, obj.position.x -.3f, obj.position.y -.25f,.6f,.5f);
    }

    private void drawPipes(float delta) {
        for (Pipe obj: oWorld.arrPipes){
            if (obj.type == Pipe.TYPE_DOWN){
                spriteBatch.draw(Assets.pipeDown,
                        obj.position.x -.5f,
                        obj.position.y -2f,
                        1,
                        4
                );
            }else{
                spriteBatch.draw(Assets.pipeUp,
                        obj.position.x -.5f,
                        obj.position.y -2f,
                        1,
                        4
                );
            }
        }
    }

    private void drawBackground(float delta) {
        spriteBatch.draw(Assets.background, 0, 0, WIDTH, HEIGHT);
    }

}
