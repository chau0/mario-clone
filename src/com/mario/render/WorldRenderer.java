package com.mario.render;


import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mario.model.Flag;
import com.mario.model.World;
import com.mario.util.Constant;
import com.mario.util.Debug;

public class WorldRenderer {


	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	

	private Batch spriteBatch;
	private World world;
	
	private ShapeRenderer shapeRenderer;

	public WorldRenderer(World world) {
		shapeRenderer = new ShapeRenderer();
		this.world = world;
		renderer = new OrthogonalTiledMapRenderer(world.getLevel().getMap(),
				Constant.UNIT_SCALE);
		spriteBatch = renderer.getBatch();
		Debug.d("screen:+"+Gdx.graphics.getWidth()+","+Gdx.graphics.getHeight());

		camera = new OrthographicCamera();
		float ratio = (float)Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
		if (ratio >= 1.7) {
			camera.setToOrtho(false, Constant.CAMERA_WIDTH_WIDE,
					Constant.CAMERA_HEIGHT);
		} else {
			camera.setToOrtho(false, Constant.CAMERA_WIDTH,
					Constant.CAMERA_HEIGHT);
		}
		Debug.d("player x:" + world.getPlayer().getPosition().toString());
		Debug.d("camera:" +camera.viewportWidth);
		Debug.d("ratio :+"+ratio);

		camera.update();

	}

	

	public void render(float delta) {
		if(world.isWorldReset){
			return;
		}
		renderer.setView(camera);
		if (world.getPlayer().getPosition().x > Constant.PLAYER_START_X) {
			float newCameraPos = world.getPlayer().getPosition().x ;
			if (newCameraPos > camera.position.x && world.getFlag().state != Flag.State.PULLED_DOWN) {
				camera.position.x = newCameraPos;
			}
		} else {
			camera.position.x = camera.viewportWidth / 2;
		}
		camera.update();
		renderer.render();
		spriteBatch.begin();
		world.draw(spriteBatch,delta,camera.position.x ,camera.viewportWidth);
		spriteBatch.end();
        
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.GRAY);
		if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
			world.renderArrow(shapeRenderer, camera.position.x -camera.viewportWidth
					/ 2, camera.position.x+camera.viewportWidth/2);
		}
		shapeRenderer.end();
		// updatePlayer(delta);
	}

	


	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public void dispose(){
		renderer.dispose();;
	}
	

}
