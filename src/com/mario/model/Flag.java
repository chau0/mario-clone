package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Flag extends Entity {
	public enum State {
		STAND_IDLE, MOVING, PULLED_DOWN
	};
	
	public State state;

	@Override
	public void loadTexture() {
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));
		entityFrame = atlas.findRegion("flag");
	}
	
	public void init(World world, float x, float y) {
		velocity.x = 0;
		velocity.y = 0;
		this.world = world;
		position.x =x;
		position.y =y;
		state =State.STAND_IDLE;
		speedY =-0.2f;
	}


	@Override
	public void draw(Batch b) {
		b.draw(entityFrame, position.x, position.y, width, height);
	}

	@Override
	public void update(float delta) {
		
		if (state == State.MOVING) {
			position.y += speedY;
			if (position.y < 5.5f) {
				position.y = 5.5f;
				state =State.PULLED_DOWN;
				world.getPlayer().setState(Player.State.pulled_flag_reverse);
				world.getPlayer().getPosition().x =position.x + width-0.5f;
			    world.getPlayer().setStartTime(System.currentTimeMillis());
			}
		}

	}

}
