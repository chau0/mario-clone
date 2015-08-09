package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mario.util.Debug;

public class Flower extends Entity {
	public static final float RUNNING_FRAME_DURATION = 0.03f;
	public static final float SPEED = 0.03f;
	private Animation flowerAnimation;
	private TextureRegion upFrame;

	public Flower(float x, float y) {
		super();
		velocity.y = SPEED;
		position.x = anchorPoint.x = x;
		position.y = anchorPoint.y = y;

	}

	@Override
	public void loadTexture() {
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));

		int numberFrames = 5;
		TextureRegion[] animationFrames = new TextureRegion[numberFrames];
		for (int i = 0; i < numberFrames; i++) {
			animationFrames[i] = atlas.findRegion("flower-0" + (i + 1));
		}
		flowerAnimation = new Animation(RUNNING_FRAME_DURATION, animationFrames);
		upFrame = animationFrames[0];
		entityFrame = upFrame;

	}

	@Override
	public void draw(Batch b) {
		entityFrame = flowerAnimation.getKeyFrame(stateTime, true);
		b.draw(entityFrame, position.x, position.y, width, height);
	}

	@Override
	public void update(float delta) {
		if (position.y > anchorPoint.y + height) {
			velocity.y = 0;
			position.y = anchorPoint.y + height;

		}
		position.add(velocity);
		stateTime += delta;
	}

}
