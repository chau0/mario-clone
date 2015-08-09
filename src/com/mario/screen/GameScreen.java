package com.mario.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.mario.controller.PlayerController;
import com.mario.launcher.MyGdxGame;
import com.mario.model.World;
import com.mario.render.WorldRenderer;

public class GameScreen implements Screen {
	private WorldRenderer renderer;
	private World world;
	private PlayerController playerController;
	private MyGdxGame game;

	private static final int MAX_FPS = 60;
	private static final float TIME_STEP = 1f / MAX_FPS;

	public GameScreen(MyGdxGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		world = new World(this);
		playerController = new PlayerController(this);
		renderer = new WorldRenderer(world);

	}
	@Override
	public void dispose() {
		world.dipose();
		renderer.dispose();
	}

	public void changeScreen() {
		game.setScreen(new LoadingScreen(game));
		this.dispose();
	}

	public World getWorld() {
		return world;
	}

	public WorldRenderer getRenderer() {
		return renderer;
	}

	@Override
	public void render(float delta) {
			Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			update(TIME_STEP);
			renderer.render(TIME_STEP);
	}

	private void update(float delta) {
		playerController.update(delta);
		world.update(delta, renderer.getCamera().position.x,
				renderer.getCamera().viewportWidth);
	}

	@Override
	public void resize(int width, int height) {

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



}
