package com.cate.flappy.Counter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.cate.flappy.Assets;

import java.util.UUID;

public class Pipe {

    public UUID id = UUID.randomUUID();
    public static int TYPE_UP = 0;
    public static int TYPE_DOWN = 1;
    public int type;

    public static float WIDTH = .85f;
    public static float HEIGHT = 4f;

    public static int STATE_NORMAL = 0;
    public static int STATE_REMOVE = 1;
    public int state;

    public static float SPEED_X = -2f;

    public Vector2 position;

    public Pipe(float x, float y, int type) {
        position = new Vector2(x, y);
        state = STATE_NORMAL;
        this.type = type;
    }

    public Pipe(float x, float y, int type, UUID id) {
        this.id = id;
        position = new Vector2(x, y);
        state = STATE_NORMAL;
        this.type = type;
    }

    public void update(Body body) {
        position.x = body.getPosition().x;
        position.y = body.getPosition().y;
    }

    public void draw(Batch batch){
            batch.draw(type == 0 ? Assets.pipeDown : Assets.pipeUp, position.x-.5f, position.y-2f,1,4);
    }

}