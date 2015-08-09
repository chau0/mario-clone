package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mario.util.Debug;

public class Bullet extends Entity {

	Animation bulletAnimation;
	TextureRegion fireFrame;
	public boolean isFired;


	public Bullet() {
		super();
		gravity = 50;
		speedX = 30;
		speedY = 10;
	}

	public void init(World world, float x, float y, boolean isFacingRight) {
		acceleration.y = -gravity;
		velocity.x = (isFacingRight ? speedX : -speedX);
		velocity.y = 0;
		this.world = world;
		position.x =x;
		position.y =y;
		runningFrameDuration = 0.01f;
	}

	@Override
	public void loadTexture() {
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));

		int numberFrames = 4;
		TextureRegion[] animationFrames = new TextureRegion[numberFrames];
		for (int i = 0; i < numberFrames; i++) {
			animationFrames[i] = atlas.findRegion("fire-0" + (i + 1));
		}
		bulletAnimation = new Animation(runningFrameDuration, animationFrames);
		entityFrame = animationFrames[0];
		fireFrame = atlas.findRegion("fire-05");
	}

	@Override
	public void draw(Batch b) {
		if (isFired) {
			entityFrame = fireFrame;
			
			
		} else {
			entityFrame = bulletAnimation.getKeyFrame(stateTime,
					true);
		}
		b.draw(entityFrame, position.x, position.y, width, height);
	}

	@Override
	public void update(float delta) {
		if (isFired && System.currentTimeMillis() - startTime > 200) {
			isRemove = true;
		}
		if (!isFired) {
			velocity.y += acceleration.y * delta;
			deltaPos.x = velocity.x *delta;
			deltaPos.y = velocity.y *delta;
			checkCollision(delta);
			
			position.x += deltaPos.x;
			position.y += deltaPos.y;
		}
		stateTime+=delta;
	
	}
	
	public void fired()
	{
		isFired = true;
		startTime = System.currentTimeMillis();
	}

	private void checkCollision(float delta) {
		boolean collisionX = false;
		if (velocity.x < 0) {
			collisionX = world.collidesLeft(position, deltaPos, width, height);
		} else {
			collisionX = world.collidesRight(position, deltaPos, width, height);
		}

		if (collisionX) {
			fired();
		}

		boolean collisionY = false;
		if (velocity.y < 0) {
			collisionY = world
					.collidesBottom(position, deltaPos, width, height,true);
		}

		if (collisionY) {
			deltaPos.y =0;
			if (velocity.y < 0) {
				velocity.y = speedY;
			}
			
		}
	}

}
