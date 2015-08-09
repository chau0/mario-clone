package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class BrickBroken  extends Entity{
	
	public static final float GRAVITY=3;
	
	public boolean isRemove;
	
	private Vector2 acceleration;

	public BrickBroken(Vector2 postition, Vector2 acceleration) {
		super();
		this.position = postition;
		this.anchorPoint.x = postition.x;
		this.anchorPoint.y = postition.y;
		this.acceleration =acceleration;
	}

	@Override
	public void loadTexture() {
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));
		entityFrame= atlas.findRegion("brick-broken");
		
	}

	@Override
	public void draw(Batch b) {
		b.draw(entityFrame, position.x, position.y, width, height);		
	}

	@Override
	public void update(float delta) {
		velocity.y -=GRAVITY*delta;
		acceleration.scl(delta);
		velocity.add(acceleration);
		position.add(velocity);
		if(Math.abs(velocity.x)<0.001f)
		{
			isRemove =true;
		}
	}

}
