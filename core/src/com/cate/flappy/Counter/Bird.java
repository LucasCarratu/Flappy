package com.cate.flappy.Counter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Bird {

    public int id;
    public static float JUMP_SPEED = 5;

    public static int STATE_NORMAL = 0;
    public static int STATE_DEAD = 1;
    public int state;

    public Vector2 position;
    public float stateTime; //indica el tiempo en el que se encuentra en cierto estado (normal o dead)
    public Body body;


    public int score;

    public Bird(float x, float y) {
        position = new Vector2(x, y);
        state = STATE_NORMAL;
    }
    public Bird(float x, float y,int id) {
        this.id = id;
        position = new Vector2(x, y);
        state = STATE_NORMAL;
    }


    // actualiza la posicion del objeto para encajar con la hitbox
    public void update(float delta, Body body) {
        position.x = body.getPosition().x;
        position.y = body.getPosition().y;
        stateTime += delta;
    }

    // cuando el pajaro choca
    public void hurt() {
        state = STATE_DEAD;
        stateTime = 0;
    }

    public void volar() {
        body.setLinearVelocity(0, Bird.JUMP_SPEED);
    }
}
