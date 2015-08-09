package com.mario.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mario.util.Constant;

public abstract class Entity implements IEntity{
	public   float runningFrameDuration = 0.025f;
	public Vector2 position;
	public Vector2 velocity;
	public float width;
	public float height;
	public Vector2 anchorPoint;
	public float stateTime;
	public TextureRegion entityFrame;
	public Vector2 deltaPos;
	public Vector2 acceleration;
	public World world;
	
	public float gravity;
	public float speedX ;
	public float speedY;
	public boolean isRemove;
	public long startTime;
	
	
	public Entity() {
		velocity = new Vector2();
		position = new Vector2();
		anchorPoint =new Vector2();
		deltaPos =new Vector2();
		acceleration = new Vector2();
		loadTexture();
		width = Constant.UNIT_SCALE * entityFrame.getRegionWidth();
		height = Constant.UNIT_SCALE * entityFrame.getRegionHeight();
	}
	
	
	public void stopHorizontal() {
		velocity.x = 0;
		acceleration.x = 0;
		deltaPos.x = 0;
	}

	public void stopVertical() {
		velocity.y = 0;
		acceleration.y = 0;
		deltaPos.y = 0;
	}
	
	public boolean checkCollides(Entity entity) {
		if (    position.x + width > entity.position.x
			 && entity.position.x + entity.width > position.x
			 && position.y + height > entity.position.y
			 && entity.position.y + entity.height > position.y) {
			return true;
		}
		return false;
	}
}
