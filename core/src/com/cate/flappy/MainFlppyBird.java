package com.cate.flappy;

import com.badlogic.gdx.Game;
import com.cate.flappy.game.screens.MenuScreen;
import com.cate.flappy.game.screens.client.WinnerScreen;
import com.cate.flappy.net.RedUtiles;

public class MainFlppyBird extends Game {

	public static final short BIRD_CATEGORY = 0x0001; // Categoría para el pájaro 1
	public static final short COUNTER_CATEGORY = 0x0004; // 0010
	public static final short PIPE_CATEGORY = 0x0008; // 0100

	@Override
	public void create() {
		Assets.load();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		RedUtiles.terminarRed();
	}
}
