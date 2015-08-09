package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Goomba  extends Entity{
	
	private Animation walkAnimation;
	private TextureRegion dieFrame;
	private TextureRegion rotateFrame;
    public boolean isRotate ;
	
	public Goomba() {
		super();
		speedX =0.08f;
		gravity =100;
	}
	
	public boolean isDied ;

	public void init(World world, float x, float y) {
		acceleration.y = -gravity;
		velocity.x =-speedX;
		velocity.y = 0;
		this.world = world;
		position.x =x;
		position.y =y;
		
	}
	@Override
	public void loadTexture() {
		runningFrameDuration =0.2f;
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));
	    int numberFrame =2;
	    TextureRegion [] animationFrame = new TextureRegion[numberFrame];
	    animationFrame[0]= atlas.findRegion("goom-ba-01");
	    animationFrame[1]= atlas.findRegion("goom-ba-02");
	    walkAnimation =new Animation(runningFrameDuration, animationFrame);
	    dieFrame =atlas.findRegion("goom-ba-03");
	    entityFrame =animationFrame[0];
	    rotateFrame =new TextureRegion(animationFrame[0]);
	    rotateFrame.flip(true,true);
		
	}
	
	public void killed(boolean isKilledByPlayer) {
		if (isKilledByPlayer) {
			isDied = true;
			startTime = System.currentTimeMillis();
		} else {
			isRotate = true;
			velocity.y =10;
			velocity.x = 0.05f;
		}
	}

	@Override
	public void draw(Batch b) {
		if (isRotate) {
			entityFrame = rotateFrame;
		} else {
			entityFrame = isDied ? dieFrame : walkAnimation.getKeyFrame(
					stateTime, true);
		}
		b.draw(entityFrame, position.x , position.y,width,height);
	}

	@Override
	public void update(float delta) {
		if (!isDied) {
			velocity.y += acceleration.y * delta;
			deltaPos.x = velocity.x;
			deltaPos.y = velocity.y * delta;
			checkCollision(delta);
			position.x += deltaPos.x;
			position.y += deltaPos.y;
			if (position.y < -10) {
				isRemove = true;
			}
			stateTime += delta;
		} else if (System.currentTimeMillis() - startTime > 500) {
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

		if (collisionX &&!isRotate) {
			velocity.x = -velocity.x;
		}

		boolean collisionY = false;
		if (velocity.y < 0) {
			collisionY = world
					.collidesBottom(position, deltaPos, width, height,true);
		}

		if (collisionY &&!isRotate) {
			stopVertical();
			acceleration.y =-gravity;
		}
	}

}
