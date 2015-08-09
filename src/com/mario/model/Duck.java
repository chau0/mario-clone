package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mario.util.Debug;

public class Duck extends Entity {

	private Animation walkAnimationRight;
	private Animation walkAnimationLeft;
	private Animation rebornAnimation;
	private TextureRegion noHeadFrame;
	private TextureRegion rotateFrame;
	public boolean isFacingRight = false;
	public boolean isRotate;

	public enum State {
		walking, noHead, reborn
	};

	public State state;

	public Duck() {
		super();
		state = State.walking;
		speedX = 0.08f;
		gravity = 100;
	}

	public void init(World world, float x, float y) {
		acceleration.y = -gravity;
		velocity.x = -speedX;
		velocity.y = 0;
		this.world = world;
		position.x = x;
		position.y = y;

	}

	@Override
	public void loadTexture() {
		runningFrameDuration = 0.2f;
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));
		int numberFrame = 2;
		TextureRegion[] walkingLeftFrame = new TextureRegion[numberFrame];
		for (int i = 0; i < numberFrame; i++) {
			walkingLeftFrame[i] = atlas.findRegion("duck-0" + (i + 1));
		}
		walkAnimationLeft = new Animation(runningFrameDuration,
				walkingLeftFrame);
		TextureRegion[] walkingRightFrame = new TextureRegion[numberFrame];
		for (int i = 0; i < numberFrame; i++) {
			walkingRightFrame[i] = new TextureRegion(walkingLeftFrame[i]);
			walkingRightFrame[i].flip(true, false);
		}
		walkAnimationRight = new Animation(runningFrameDuration,
				walkingRightFrame);

		noHeadFrame = atlas.findRegion("duck-05");

		TextureRegion[] rebornFrame = new TextureRegion[numberFrame];
		rebornFrame[0] = noHeadFrame;
		rebornFrame[1] = atlas.findRegion("duck-06");
		entityFrame = noHeadFrame;
		rotateFrame = new TextureRegion(noHeadFrame);
		rotateFrame.flip(true, true);

	}

	@Override
	public void draw(Batch b) {
		if (isRotate) {
            entityFrame = rotateFrame;
		}else if (state == State.walking) {
			entityFrame = isFacingRight ? walkAnimationRight.getKeyFrame(
					stateTime, true) : walkAnimationLeft.getKeyFrame(stateTime,
					true);
		} else if (state == State.noHead) {
			entityFrame = noHeadFrame;
		} else {
			entityFrame = rebornAnimation.getKeyFrame(stateTime, true);
		}

		b.draw(entityFrame, position.x, position.y, width, height);

	}

	@Override
	public void update(float delta) {
		velocity.y += acceleration.y * delta;
		deltaPos.x = velocity.x;
		deltaPos.y = velocity.y * delta;
		checkCollision(delta);
		position.x += deltaPos.x;
		position.y += deltaPos.y;
		stateTime += delta;
		if (position.y < -10) {
			isRemove = true;
		}

	}

	private void checkCollision(float delta) {
		boolean collisionX = false;
		if (velocity.x < 0) {
			collisionX = world.collidesLeft(position, deltaPos, width, height);
		} else {
			collisionX = world.collidesRight(position, deltaPos, width, height);
		}

		if (collisionX && !isRotate) {
			velocity.x = -velocity.x;
			isFacingRight = !isFacingRight;
		}

		boolean collisionY = false;
		if (velocity.y < 0) {
			collisionY = world.collidesBottom(position, deltaPos, width,
					height, true);
		}

		if (collisionY && !isRotate) {
			stopVertical();
			acceleration.y = -gravity;
		}
	}

	public void injuried() {
		if (state == State.walking) {
			state = State.noHead;
			velocity.x = 0;
		}

	}

	public void killed(boolean isRight) {
		isRotate = true;
		velocity.y = 10;
		if (isRight) {
			velocity.x =0.05f;
		}
	}

}
