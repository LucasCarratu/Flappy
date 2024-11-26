package com.cate.flappy.net;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;


public class ClientBird extends Sprite {

    public boolean dead;
    public int score;

    private Animation<TextureAtlas.AtlasRegion> animation;
    private float stateTime;

    public ClientBird(Animation<TextureAtlas.AtlasRegion> atlas, float x, float y) {
        setPosition(x,y);


        setRegion(atlas.getKeyFrame(0, true));
        setBounds(0,0,.6f,.5f);
        animation = atlas;
        stateTime = 0f;
    }


    public void update(float deltaTime) {
        stateTime += deltaTime;
        setRegion(animation.getKeyFrame(stateTime, true));
    }

}
