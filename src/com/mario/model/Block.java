package com.mario.model;

import java.util.Iterator;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mario.render.WorldRenderer;
import com.mario.util.Constant;
import com.mario.util.TileId;

public class Block {

	public static final float SPEED = 0.15f;
	public static final float FRAME_DURATION = 0.3f;
	public Vector2 position;
	public Vector2 velocity;
	private float stateTime;
	private Animation flashingAnimation;
	private TextureRegion blockFrame;
	private TextureRegion nonCoinFrame;
	public boolean isOutOfItem;
	private TextureRegion brickFrame1;
	private TextureRegion brickFrame2;

	public float width;
	public float height;
	public Vector2 anchorPoint;

	private int coint = 1;

	private int tileId;
	private TextureRegion brickBroken;
	public boolean isHidden;

	public static enum BLOCK_TYPE {
		COIN, BRICK, MUSH_ROOM_COIN, BRICK_COIN
	};

	public BLOCK_TYPE type;

	Pool<Coin> poolCoin = new Pool<Coin>() {

		@Override
		protected Coin newObject() {
			// TODO Auto-generated method stub
			return new Coin();
		}

	};

	private Array<Coin> listCoin;
    private World world;
	
	public Block(World world,float x, float y, BLOCK_TYPE type, int tileId) {
		isOutOfItem = false;
		position = new Vector2();
		anchorPoint = new Vector2();
		position.x = anchorPoint.x = x;
		position.y = anchorPoint.y = y;
		velocity = new Vector2();
		loadTexture();
		width = Constant.UNIT_SCALE * nonCoinFrame.getRegionWidth();
		height = Constant.UNIT_SCALE * nonCoinFrame.getRegionHeight();
		listCoin = new Array<Coin>();
		this.type = type;
		this.tileId = tileId;
		this.world =world;
	}

	private void loadTexture() {
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));
		brickFrame1 = atlas.findRegion("brick-block-01");
		brickFrame2 = atlas.findRegion("brick-block-02");
		nonCoinFrame = atlas.findRegion("coin-block-01");
		int numberFrame = 3;
		TextureRegion[] flashingFrame = new TextureRegion[numberFrame];
		for (int i = 0; i < numberFrame; i++) {
			flashingFrame[i] = atlas.findRegion("coin-block-0" + (i + 2));
		}

		flashingAnimation = new Animation(FRAME_DURATION, flashingFrame);
		brickBroken =atlas.findRegion("brick-broken");

	}

	public boolean isCollidesWithEntity(float x, float y,float width, float height) {
		if (position.x +this.width> x && x+width > position.x 
				&& position.y +this.height>y&& y+height > position.y ) {
			return true;
		}

		return false;
	}

	public void draw(Batch b, float delta) {
		if (!isHidden) {
			if (type == BLOCK_TYPE.COIN || type == BLOCK_TYPE.MUSH_ROOM_COIN) {
				if (isOutOfItem) {
					blockFrame = nonCoinFrame;
				} else {
					blockFrame = flashingAnimation.getKeyFrame(stateTime, true);
				}
				if (listCoin.size > 0) {
					Iterator<Coin> it = listCoin.iterator();
					while (it.hasNext()) {
						Coin coin = it.next();
						coin.draw(b);
					}
				}
			} else if (type == BLOCK_TYPE.BRICK) {
				if (tileId == TileId.BRICK_BLOCK_1_1) {
					blockFrame = brickFrame1;
				} else {
					blockFrame = brickFrame2;
				}
			}

			b.draw(blockFrame, position.x, position.y, width, height);

			stateTime += delta;
		}
	}

	public void vibrate() {
		if (type == BLOCK_TYPE.MUSH_ROOM_COIN) {
			if (!isOutOfItem)
				velocity.y = SPEED;
		} else if (type == BLOCK_TYPE.COIN) {
			if (!isOutOfItem) {
				world.playCoinSound();
				velocity.y = SPEED;
				Coin coin = poolCoin.obtain();
				coin.setPosition(position.x, position.y + 3f);
				coin.release();
				listCoin.add(coin);
				coint--;
				if (coint <= 0) {
					coint = 0;
					isOutOfItem = true;
				}
			}
		} else if (type == BLOCK_TYPE.BRICK) {
			velocity.y = SPEED;
			world.playBumpSound();
		}
	}

	public void update(float delta) {
		if (!isHidden) {
			if (velocity.y != 0) {
				// velocity.scl(delta);
				position.add(velocity);
				if (position.y > anchorPoint.y + 1f) {
					velocity.y = -SPEED;
				}

				if (velocity.y < 0 && position.y <= anchorPoint.y) {
					velocity.y = 0;
					position.y = anchorPoint.y;
					if (type == BLOCK_TYPE.MUSH_ROOM_COIN) {
						isOutOfItem = true;
						world.creatNewSpecialItem(anchorPoint.x, anchorPoint.y);
						world.playPowerUpAppear();
					}

				}

			}

			if (type == BLOCK_TYPE.COIN) {
				if (listCoin.size > 0) {
					Iterator<Coin> it = listCoin.iterator();
					while (it.hasNext()) {
						Coin coin = it.next();
						coin.update(delta);
						if (coin.isFinish) {
							it.remove();
						}

					}
				}
			}
		}
	}
	

}
