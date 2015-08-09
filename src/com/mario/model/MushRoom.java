package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mario.util.Constant;
import com.mario.util.Debug;

public class MushRoom {
	public static final float SPEED = 0.15f;
	public static final float GRAVITY = 3;
	public float width;
	public float height;
	private boolean isPrepareMove;
	
	public Vector2 position;
	public Vector2 velocity;
	public Vector2 anchorPoint;
	private TextureRegion frame;
	private World world;

	public MushRoom(World world, float x, float y) {
		position = new Vector2();
		anchorPoint = new Vector2();
		velocity = new Vector2();
		position.x = anchorPoint.x = x;
		position.y = anchorPoint.y = y;
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));
		frame = atlas.findRegion("mushroom");
		width = Constant.UNIT_SCALE * frame.getRegionWidth();
		height = Constant.UNIT_SCALE * frame.getRegionHeight();
		velocity.y = 0.03f;
		this.world = world;
	}

	public void update(float delta) {
		if (position.y > anchorPoint.y + height) {
			if (!isPrepareMove) {
				position.y = anchorPoint.y + height;
				velocity.x = SPEED;
				isPrepareMove = true;
			}
		}
		if (isPrepareMove) {
			velocity.y -= GRAVITY * delta;
			checkCollision(delta);
		}
		position.add(velocity);
	}

	private void checkCollision(float delta) {
		boolean collisionX = false;
		if (velocity.x < 0) {
			collisionX = world.collidesLeft(position, velocity, width, height);
		} else {
			collisionX = world.collidesRight(position, velocity, width, height);
		}

		if (collisionX) {
			velocity.x = -velocity.x;
		}

		boolean collisionY = false;
		if (velocity.y < 0) {
			collisionY = collidesBottom(position, velocity, width, height,
					false);
		}

		if (collisionY) {
			Array<Block> listBlock = world.getBlockCollided(position, velocity, width,
					height);
			if (listBlock.size > 0) {
				for (Block block : listBlock) {
					if (block != null && block.velocity.y != 0) {
						// + "," + block.velocity.toString());
						velocity.y += GRAVITY * delta * 12;
						if (position.x + 0.5f < block.position.x
								|| block.velocity.y < 0) {
							velocity.x = -SPEED;
						}
						break;
					} else {
						velocity.y = 0;
					}
				}
			} else {
				velocity.y = 0;
			}
		}
	}

	public boolean collidesBottom(Vector2 position, Vector2 velocity,
			float width, float height, boolean isPrint) {
		float newPositionY = position.y + velocity.y;
		for (float step = width; step >= 0; step--) {
			if (world.checkCollidesBlock(position.x + step, newPositionY) > 0) {
				return true;
			}
			if (world.isCellBlocked(position.x + step, newPositionY)) {
				return true;
			}

		}
		return false;
	}

	public void draw(Batch b) {
		b.draw(frame, position.x, position.y, width, height);
	}
	
	public void dispose(){
		 position=null;
		 velocity=null;
		 anchorPoint=null;
		 frame=null;
	}

}
