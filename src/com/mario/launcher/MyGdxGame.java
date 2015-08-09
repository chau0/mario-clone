package com.mario.launcher;

import com.badlogic.gdx.Game;
import com.mario.screen.GameScreen;

public class MyGdxGame extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen(this));
	}

}
