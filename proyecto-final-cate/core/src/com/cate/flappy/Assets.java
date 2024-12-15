package com.cate.flappy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import sun.font.TrueTypeFont;

public class Assets {

    public static BitmapFont font;
    public static BitmapFont font25;
    private static final GlyphLayout glyphLayout = new GlyphLayout();  //ayuda a conocer el tamaño del texto para centrarlo

    public static Animation<TextureAtlas.AtlasRegion> bird; //importando los assets del atlas

    public static TextureRegion background;
    public static TextureRegion gameOver;
    public static TextureRegion getReady;
    public static TextureRegion tap;
    public static TextureRegion pipeDown;
    public static TextureRegion pipeUp;

    public static void load(){                  //cargar las imagenes
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/atlasMap.txt"));

        background = atlas.findRegion("background");
        gameOver = atlas.findRegion("gameOver");
        getReady = atlas.findRegion("getReady");
        tap = atlas.findRegion("tap");
        pipeDown = atlas.findRegion("pipeDown");
        pipeUp = atlas.findRegion("pipeUp");

        bird = new Animation<TextureAtlas.AtlasRegion>(
                .3f,                          //especificamos la duracion de cada frame (.3 segundos)
                atlas.findRegion("bird1"),
                atlas.findRegion("bird2"),
                atlas.findRegion("bird3"));


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 75;
        font = generator.generateFont(parameter); // font size 12 pixels


        FreeTypeFontGenerator.FreeTypeFontParameter parameter25 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter25.size = 25;
        font25 = generator.generateFont(parameter25); // font size 12 pixels
        /*font = new BitmapFont(Gdx.files.internal("data/font.fnt"));
        font.getData().scale(7f);*/
    }

    public static float getTextWidth(String text){              //esta funcion cuenta los pixeles de un string para poder centrarlo en la UI
        glyphLayout.setText(font,text);
        return glyphLayout.width;
    }
}
