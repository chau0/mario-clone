package com.mario.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mario.util.Constant;
import com.mario.util.Debug;

public class Player {
	public static final float MAX_VELOCITY = 0.05f;
	public static final float DAMPING = 0.87f;

	public static final float MAX_JUMP_SPEED = 0.2f;
	public static final long LONG_JUMP_PRESS = 150l;
	public static final float MAX_SPEED_X = 15f;
	public static final float MAX_SPEED_Y = 40f;

	public static final float WALK_LENGTH = 11.5f;
	private float startPosWalk;

	public enum State {
		Standing, Walking, Jumping, Falling, Growing, Fire, Injuried, Died, pulled_flag, pulled_flag_reverse, Automatic_walking, Invisble
	}

	public static final int LEVEL_SMALL = 0;
	public static final int LEVEL_BIG = 1;
	public static final int LEVEL_FIRE = 2;
	public int currentLevel;
	private int numberLevel;

	private static final float DAMP = 0.90f;
	public static final float RUNNING_FRAME_DURATION = 0.1f;

	private State state;
	private boolean facingRight;
	private boolean grounded;
	private float stateTime;
	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;

	private float width;
	private float height;

	/* Textures for Player */
	private TextureRegion playerDiedFrame;
	private TextureRegion[] playerIdleLeft;
	private TextureRegion[] playerIdleRight;
	private TextureRegion[] playerJumpLeft;
	private TextureRegion[] playerJumpRight;
	private TextureRegion[] playerPullFlag;
	private TextureRegion[] playerPullFlagReverse;
	/* Animations for Player */
	private Animation[] walkLeftAnimation;
	private Animation[] walkRightAnimation;
	private TextureRegion playerFrame;
	private Animation[] growingAnimationRight;
	private Animation[] growingAnimatiionLeft;

	private Animation injuriedAnimationLeft;
	private Animation injuriedAnimationRight;

	public boolean canJump = true;
	private World world;
	private long startTime;
	public Vector2 deltaPos;

	public TextureRegion playerFireRight;
	public TextureRegion playerFireLeft;

	private State lastState;

	public boolean isInjuried;
	private float lastTimeDraw;

	public Player(World world) {
		numberLevel = 3;
		position = new Vector2();
		position.x = Constant.PLAYER_START_X;
		position.y = Constant.PLAYER_START_Y;
		deltaPos = new Vector2();
		velocity = new Vector2();
		acceleration = new Vector2();
		state = State.Standing;
		facingRight = true;
		stateTime = 0;
		grounded = true;
		loadTextures();
		width = Constant.UNIT_SCALE * playerFrame.getRegionWidth();
		height = Constant.UNIT_SCALE * playerFrame.getRegionHeight();
		this.world = world;
		// changeFrameDuration(0.1f);

		stopHorizontal();
		stopVertical();

	}

	public void loadTextures() {

		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("mario-animation.pack"));
		playerIdleLeft = new TextureRegion[numberLevel];
		playerIdleRight = new TextureRegion[numberLevel];
		playerJumpLeft = new TextureRegion[numberLevel];
		playerJumpRight = new TextureRegion[numberLevel];
		walkLeftAnimation = new Animation[numberLevel];
		walkRightAnimation = new Animation[numberLevel];
		growingAnimatiionLeft = new Animation[numberLevel];
		growingAnimationRight = new Animation[numberLevel];

		playerPullFlag = new TextureRegion[numberLevel];
		playerPullFlagReverse = new TextureRegion[numberLevel];

		for (int k = 0; k < numberLevel; k++) {
			playerPullFlag[k] = atlas.findRegion("mario-pull-" + k);
			playerPullFlagReverse[k] = new TextureRegion(playerPullFlag[k]);
			playerPullFlagReverse[k].flip(true, false);
		}

		for (int k = 0; k < numberLevel; k++) {
			/* Standing */
			playerIdleRight[k] = atlas.findRegion(String
					.format("mario-%d-0", k));
			playerIdleLeft[k] = new TextureRegion(playerIdleRight[k]);
			playerIdleLeft[k].flip(true, false);

			// jumping
			playerJumpRight[k] = atlas.findRegion(String
					.format("mario-%d-5", k));
			playerJumpLeft[k] = new TextureRegion(playerJumpRight[k]);
			playerJumpLeft[k].flip(true, false);

			int numberFrame = 3;
			TextureRegion[] walkRightFrames = new TextureRegion[numberFrame];
			for (int i = 0; i < numberFrame; i++) {
				walkRightFrames[i] = atlas.findRegion(String.format(
						"mario-%d-%d", k, i + 1));
			}
			// walkRightFrames[0] =atlas.findRegion(name)

			walkRightAnimation[k] = new Animation(RUNNING_FRAME_DURATION,
					walkRightFrames);

			TextureRegion[] walkLeftFrames = new TextureRegion[numberFrame];
			for (int i = 0; i < numberFrame; i++) {
				walkLeftFrames[i] = new TextureRegion(walkRightFrames[i]);
				walkLeftFrames[i].flip(true, false);
			}

			walkLeftAnimation[k] = new Animation(RUNNING_FRAME_DURATION,
					walkLeftFrames);
		}

		playerFrame = playerIdleLeft[0];

		// player fire
		playerFireRight = atlas.findRegion("mario-fire");
		playerFireLeft = new TextureRegion(playerFireRight);
		playerFireLeft.flip(true, false);

		// growing 1
		int numberGrowingFrame = 2;
		TextureRegion[] growingFrameRight = new TextureRegion[numberGrowingFrame];
		growingFrameRight[0] = atlas.findRegion("mario-1-6");
		growingFrameRight[1] = playerIdleRight[1];
		growingAnimationRight[0] = new Animation(RUNNING_FRAME_DURATION,
				growingFrameRight);
		TextureRegion[] growingFrameLeft = new TextureRegion[numberGrowingFrame];
		growingFrameLeft[0] = new TextureRegion(growingFrameRight[0]);
		growingFrameLeft[0].flip(true, false);
		growingFrameLeft[1] = new TextureRegion(growingFrameRight[1]);
		growingFrameLeft[1].flip(true, false);
		growingAnimatiionLeft[0] = new Animation(RUNNING_FRAME_DURATION,
				growingFrameLeft);

		// growing 2
		numberGrowingFrame = 4;
		growingFrameRight = new TextureRegion[numberGrowingFrame];
		growingFrameRight[0] = atlas.findRegion("mario-2-1");
		growingFrameRight[1] = atlas.findRegion("mario-black-02");
		growingFrameRight[2] = atlas.findRegion("mario-green-02");
		growingFrameRight[3] = atlas.findRegion("mario-yellow-02");

		growingAnimationRight[1] = new Animation(RUNNING_FRAME_DURATION,
				growingFrameRight);
		growingFrameLeft = new TextureRegion[numberGrowingFrame];
		growingFrameLeft[0] = new TextureRegion(growingFrameRight[0]);
		growingFrameLeft[1] = new TextureRegion(growingFrameRight[1]);
		growingFrameLeft[2] = new TextureRegion(growingFrameRight[2]);
		growingFrameLeft[3] = new TextureRegion(growingFrameRight[3]);

		growingFrameLeft[0].flip(true, false);
		growingFrameLeft[1].flip(true, false);
		growingFrameLeft[2].flip(true, false);
		growingFrameLeft[3].flip(true, false);

		growingAnimatiionLeft[1] = new Animation(RUNNING_FRAME_DURATION,
				growingFrameLeft);

		int numberFrame = 3;
		TextureRegion[] injuriedFrameRight = new TextureRegion[numberFrame];
		injuriedFrameRight[0] = atlas.findRegion("mario-injuried-1");
		injuriedFrameRight[1] = atlas.findRegion("mario-injuried-2");
		injuriedFrameRight[2] = atlas.findRegion("mario-injuried-3");

		injuriedAnimationRight = new Animation(0.01f, injuriedFrameRight);

		TextureRegion[] injuriedFrameLeft = new TextureRegion[numberFrame];
		injuriedFrameLeft[0] = new TextureRegion(injuriedFrameRight[0]);
		injuriedFrameLeft[0].flip(true, false);
		injuriedFrameLeft[1] = new TextureRegion(injuriedFrameRight[1]);
		injuriedFrameLeft[1].flip(true, false);

		injuriedFrameLeft[2] = new TextureRegion(injuriedFrameRight[2]);
		injuriedFrameLeft[2].flip(true, false);

		injuriedAnimationLeft = new Animation(RUNNING_FRAME_DURATION,
				injuriedFrameLeft);
		playerDiedFrame = atlas.findRegion("mario-0-6");

	}

	public void changeFrameDuration(float duration) {
		playerFrame = facingRight ? playerIdleRight[currentLevel]
				: playerIdleLeft[currentLevel];
		if (facingRight)
			walkRightAnimation[currentLevel].setFrameDuration(duration);
		else
			walkLeftAnimation[currentLevel].setFrameDuration(duration);

	}

	public void draw(Batch sb, float delta) {
		if (state == State.Invisble) {
			return;
		}

		playerFrame = facingRight ? playerIdleRight[currentLevel]
				: playerIdleLeft[currentLevel];
		if (state == State.Died) {
			playerFrame = playerDiedFrame;
		} else if (state == State.Injuried) {
			playerFrame = facingRight ? injuriedAnimationRight.getKeyFrame(
					stateTime, true) : injuriedAnimationLeft.getKeyFrame(
					stateTime, true);
		} else if (state == State.Fire) {
			playerFrame = facingRight ? playerFireRight : playerFireLeft;
		} else if (state == State.Walking || state == State.Automatic_walking) {
			playerFrame = facingRight ? walkRightAnimation[currentLevel]
					.getKeyFrame(stateTime, true)
					: walkLeftAnimation[currentLevel].getKeyFrame(stateTime,
							true);

		} else if (state == State.Jumping) {
			playerFrame = facingRight ? playerJumpRight[currentLevel]
					: playerJumpLeft[currentLevel];
		} else if (state == State.Falling) {
			playerFrame = facingRight ? playerJumpRight[currentLevel]
					: playerJumpLeft[currentLevel];

		} else if (state == State.Growing) {
			playerFrame = facingRight ? growingAnimationRight[currentLevel - 1]
					.getKeyFrame(stateTime, true)
					: growingAnimatiionLeft[currentLevel - 1].getKeyFrame(
							stateTime, true);
		} else if (state == State.pulled_flag) {
			playerFrame = playerPullFlag[currentLevel];
		} else if (state == State.pulled_flag_reverse) {
			playerFrame = playerPullFlagReverse[currentLevel];
		}
		if (isInjuried) {
			if (stateTime - lastTimeDraw > 0.05f) {
				lastTimeDraw = stateTime;
				sb.draw(playerFrame, position.x, position.y, width, height);
			}
		} else {
			sb.draw(playerFrame, position.x, position.y, width, height);
		}
		stateTime += delta;
	}

	public void stopHorizontal() {
		velocity.x = 0;
		acceleration.x = 0;
		deltaPos.x = 0;
		changeFrameDuration(RUNNING_FRAME_DURATION);
	}

	public void stopVertical() {
		velocity.y = 0;
		acceleration.y = 0;
		deltaPos.y = 0;
		// Debug.d("Stop  vertical----");
	}

	public void update(float delta, float cameraX, float cameraWidth) {
		long currentTime = System.currentTimeMillis();
		if (state == State.Invisble) {

		} else if (state == State.Growing) {
			if (currentTime - startTime >= 500) {
				setState(lastState);
			}
		} else if (state == State.Injuried) {
			if (currentTime - startTime >= 1000) {
				isInjuried = true;
				levelDown();
				setState(State.Standing);
			}
		} else if (state == State.pulled_flag) {
			if (position.y > 5f) {
				position.y -= 0.2f;
			}
		} else if (state == State.pulled_flag_reverse) {
			if (currentTime - startTime > 500) {
				state = State.Automatic_walking;
				facingRight = true;
				startPosWalk = position.x;
			}
		} else {
			acceleration.y -= Constant.GRAVITY;
			if (acceleration.y > 2500) {
				acceleration.y = 2500;
			} else if (acceleration.y < -Constant.GRAVITY) {
				acceleration.y = -Constant.GRAVITY;
			}
			Debug.d(position.toString() + "," + velocity.toString() + ","
					+ acceleration.toString() + "," + deltaPos.toString() + ","
					+ delta);
			acceleration.scl(delta);

			if (acceleration.y > 2500) {
				acceleration.y = 2500;
			} else if (acceleration.y < -Constant.GRAVITY) {
				acceleration.y = -Constant.GRAVITY;
			}
			velocity.add(acceleration);

			if (state == State.Automatic_walking) {
				velocity.x = 3f;
			}
			// Debug.d(position.toString() + "," + velocity.toString() + ","
			// + acceleration.toString() + "," + deltaPos.toString() + ","
			// + delta);
			if (velocity.x > MAX_SPEED_X) {
				velocity.x = MAX_SPEED_X;
				changeFrameDuration(0.05f);
			} else if (velocity.x < -MAX_SPEED_X) {
				velocity.x = -MAX_SPEED_X;
				changeFrameDuration(0.05f);
			}

			if (velocity.y > MAX_SPEED_Y) {
				velocity.y = MAX_SPEED_Y;
			} else if (velocity.y < -MAX_SPEED_Y) {
				velocity.y = -MAX_SPEED_Y;
			}
			deltaPos.x = velocity.x * delta;
			deltaPos.y = velocity.y * delta;

			if (Math.abs(deltaPos.y) > 10) {
				stopVertical();
			}
			if (state != State.Died) {
				checkCollision();
			}
			if (acceleration.x == 0) {
				velocity.x *= DAMP;
			}
			// Debug.d(position.toString()+","+velocity.toString()+","+acceleration.toString()+","+deltaPos.toString()+","+delta);
			if (Math.abs(velocity.x) < 0.2f && Math.abs(deltaPos.x) < 0.003f) {
				if (checkCanChangeState()) {
					setState(Player.State.Standing);
					stopHorizontal();
				}
			}

			if (position.y < -10) {
				// position.x = Constant.PLAYER_START_X;
				// position.y = Constant.PLAYER_START_Y + 5;
				// currentLevel = LEVEL_SMALL;
				// width = Constant.UNIT_SCALE
				// * playerIdleLeft[0].getRegionWidth();
				// height = Constant.UNIT_SCALE
				// * playerIdleLeft[0].getRegionHeight();
				// world.formatTile();
				// stopHorizontal();
				// stopVertical();ss
				//
				Debug.d(position.toString());
				state = State.Standing;
				Debug.d("reset ------------------------------");
				world.reset();

			} else {
				if (world.getFlag().state == Flag.State.STAND_IDLE
						&& position.x + deltaPos.x + width > world.getFlag().position.x
								+ world.getFlag().width) {
					Debug.d("flag pulled ------------------");
					stopHorizontal();
					deltaPos.x = world.getFlag().position.x
							+ world.getFlag().width - position.x - width;
					world.getFlag().state = Flag.State.MOVING;
					state = State.pulled_flag;
					position.x = world.getFlag().position.x
							+ world.getFlag().width - width + 0.7f;
					world.playFlagPole();
				} else if (state != State.Died) {
					if (position.x + deltaPos.x < cameraX - cameraWidth / 2) {
						stopHorizontal();
					}
					position.x = position.x + deltaPos.x;
				}
				position.y = position.y + deltaPos.y;
			}
			if (state == State.Automatic_walking) {
				if (position.x - startPosWalk > WALK_LENGTH) {
					state = State.Invisble;
					world.playStageClearSound();
					world.stopThemeMusic();
				}
			} else if (state == State.Fire && currentTime - startTime > 50) {
				state = lastState;
			}
			if (isInjuried && currentTime - startTime > 5000) {
				isInjuried = false;
			}

		}

	}

	private boolean checkCanChangeState() {
		if (state == State.Jumping || state == State.Fire
				|| state == State.Injuried || state == State.Died
				|| state == State.pulled_flag || state == State.Invisble) {
			return false;
		}
		return true;
	}

	private void checkCollision() {
		boolean collisionX = false;
		if (velocity.x < 0) {
			collisionX = world.collidesLeft(position, deltaPos, width, height);
		} else {
			collisionX = world.collidesRight(position, deltaPos, width, height);
		}

		if (collisionX && state != State.Automatic_walking) {
			stopHorizontal();
		}

		boolean collisionY = false;
		if (velocity.y < 0) {
			collisionY = world.collidesBottom(position, deltaPos, width,
					height, false);
		} else {
			collisionY = world.collidesTop();
		}

		if (collisionY) {
			if (velocity.y < 0) {
				if (state != null && state == State.Jumping) {
					state = State.Walking;

				}
				canJump = true;
			}
			stopVertical();

		}

	}

	public void levelUp() {
		world.playPowerUp();
		setState(State.Growing);
		this.startTime = System.currentTimeMillis();

		if (currentLevel == LEVEL_SMALL) {
			width = Constant.UNIT_SCALE
					* playerIdleLeft[LEVEL_BIG].getRegionWidth();
			height = Constant.UNIT_SCALE
					* playerIdleLeft[LEVEL_BIG].getRegionHeight();
		}

		currentLevel++;

	}

	public void levelDown() {
		width = Constant.UNIT_SCALE
				* playerIdleLeft[LEVEL_SMALL].getRegionWidth();
		height = Constant.UNIT_SCALE
				* playerIdleLeft[LEVEL_SMALL].getRegionHeight();

		currentLevel--;

	}

	public void injuried() {
		// state =State.Injuried;
		Debug.d(position.toString() + "," + velocity.toString() + ","
				+ acceleration.toString() + "," + deltaPos.toString());

		if (!isInjuried) {
			if (currentLevel == LEVEL_SMALL) {
				if (state != State.Died) {
					state = State.Died;
					position.y = Constant.PLAYER_START_Y;
					velocity.y = 40;
					acceleration.y = -Constant.GRAVITY;
					world.playMarioDieSound();
				}
			} else {
				state = State.Injuried;
				startTime = System.currentTimeMillis();
				world.playerPowerDownSound();
			}
		}
	}

	public void jump() {
		canJump = false;
		setState(Player.State.Jumping);
		Debug.d("player jump" + position.toString() + "," + velocity.toString()
				+ "," + acceleration.toString() + "," + deltaPos.toString());
		velocity.y = 30;
		if (velocity.x != 0) {
			velocity.x = facingRight ? 5 : -5;
			acceleration.x = 0;
		}
		acceleration.y = -Constant.GRAVITY;

	}

	public boolean checkCollidesWithEntity(float entityX, float entityY,
			float entityWidth, float entityheight) {

		if (position.x + width > entityX && entityX + entityWidth > position.x
				&& position.y + height > entityY
				&& entityY + entityheight > position.y) {
			return true;
		}
		return false;
	}

	/**************************************************** Getters/Setters *******************************************************/

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}

	public State getState() {
		return state;
	}

	public void setLastState(State lastState) {
		this.lastState = lastState;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isFacingRight() {
		return facingRight;
	}

	public void setFacingRight(boolean facingRight) {
		this.facingRight = facingRight;
	}

	public boolean isGrounded() {
		return grounded;
	}

	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(float x, float y) {
		this.position = new Vector2(x, y);
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public State getLastState() {
		return lastState;
	}

	public long getStartTime() {
		return startTime;
	}

	public void dipose() {
		playerDiedFrame = null;
		playerIdleLeft = null;
		playerIdleRight = null;
		playerJumpLeft = null;
		playerJumpRight = null;
		playerPullFlag = null;
		playerPullFlagReverse = null;
		/* Animations for Player */
		walkLeftAnimation = null;
		walkRightAnimation = null;
		playerFrame = null;
		growingAnimationRight = null;
		growingAnimatiionLeft = null;

		injuriedAnimationLeft = null;
		injuriedAnimationRight = null;

		deltaPos = null;

		playerFireRight = null;
		playerFireLeft = null;

	}

}
