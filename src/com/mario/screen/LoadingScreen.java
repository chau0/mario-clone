package com.mario.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mario.launcher.MyGdxGame;
import com.mario.util.Debug;

public class LoadingScreen implements Screen{
	private Texture loadingImage;
	 private OrthographicCamera camera;
	 private SpriteBatch batch;
	 private float deltaTime;
	 private MyGdxGame game;
	 public LoadingScreen(MyGdxGame game) {
		 this.game =game;
	}

	@Override
	public void show() {
		 loadingImage =  new Texture(Gdx.files.internal("loading_screen.png"));
		 camera = new OrthographicCamera();
		 camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		 batch = new SpriteBatch();
	}

	@Override
	public void render(float delta) {
		 Gdx.gl.glClearColor(0, 0, 0.2f, 1);
	     Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	     camera.update();
	     batch.setProjectionMatrix(camera.combined);
	     batch.begin();
	     batch.draw(loadingImage,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	     batch.end();
	     deltaTime += delta;
	     if( deltaTime>3f){
	    	 game.setScreen(new GameScreen(game));
	    	 dispose();
	    	 Debug.d("creat new game");
	     }
	}



	@Override
	public void dispose() {
		camera =null;
		loadingImage.dispose();
		batch.dispose();
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
