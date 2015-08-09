package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mario.render.WorldRenderer;
import com.mario.util.Constant;

public class Coin {

	public static final float SPEED = 0.5f;
	public static final float FRAME_DURATION=0.07f;

	private Vector2 position;
	private Vector2 velocity;
	private float width;
	private float height;
	private Vector2 anchorPoint;
	public boolean isFinish;
	
	public float stateTime;
	Animation flyingAnimation;
	TextureRegion coinFrame;
	

	public Coin() {
		velocity = new Vector2();
		loadTexture();
		width = Constant.UNIT_SCALE * coinFrame.getRegionWidth();
		height = Constant.UNIT_SCALE * coinFrame.getRegionHeight();
	}
	
	private void loadTexture()
	{
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));
		
		int numberFrame =4;
		TextureRegion[] flyingFrame =new TextureRegion[numberFrame];
		for(int i=0;i<numberFrame;i++)
		{
			flyingFrame[i] =atlas.findRegion("coin-0"+(i+1));
		}
		flyingAnimation =new Animation(FRAME_DURATION,flyingFrame);
		coinFrame =flyingFrame[0];
		

	}

	public void setPosition(float x, float y) {
		position = new Vector2();
		anchorPoint =new Vector2();
		position.x = anchorPoint.x = x;
		position.y = anchorPoint.y = y;
	}
	
	public void release() {
		velocity.y = SPEED;
	}
	
	
	public void draw(Batch b) {
		coinFrame = flyingAnimation.getKeyFrame(stateTime,true);
		b.draw(coinFrame, position.x, position.y, width, height);
	}

	public void update(float delta) {
		if (velocity.y != 0) {
			position.add(velocity);

			if (position.y > anchorPoint.y + 5) {
				velocity.y = -SPEED;
			}

			if (position.y < anchorPoint.y) {
				isFinish = true;
				velocity.y = 0;
				position.y =-10;
			}
			stateTime+=delta;
		}
		
	}

}
