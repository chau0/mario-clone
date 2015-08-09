package com.mario.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.mario.model.Player;
import com.mario.model.World;
import com.mario.model.Player.State;
import com.mario.screen.GameScreen;

public class PlayerController implements InputProcessor {
	private Player player;
	private static final float ACCELERATION = 15;
	private boolean isTouchRight, isTouchLeft;


	private Pool<Vector3> vector3_pool = new Pool<Vector3>() {
		@Override
		protected Vector3 newObject() {
			return new Vector3();
		}
	};

	private Pool<HoldingKey> keyPool = new Pool<HoldingKey>() {
		@Override
		protected HoldingKey newObject() {
			return new HoldingKey();
		}
	};
	
	private HoldingKey holdingKey;

	private World world;
	private GameScreen scr;

	public PlayerController(GameScreen scr) {
		this.scr = scr;
		this.world = scr.getWorld();
		this.player = world.getPlayer();
		Gdx.input.setInputProcessor(this);
	
	}

	// ** Key presses and touches **************** //

	public void leftPressed() {
		if (!Player.State.Jumping.equals(player.getState())
				&& !Player.State.Growing.equals(player.getState())) {
			player.setState(Player.State.Walking);
		}
		player.setFacingRight(false);
		player.getAcceleration().x = -ACCELERATION;
	}

	public void rightPressed() {
		if (player.getState() !=State.Jumping
				 &&player.getState() != State.Fire
				) {
			player.setState(Player.State.Walking);
		}
		player.setFacingRight(true);
		player.getAcceleration().x = ACCELERATION;
	}

	public void firePressed() {
		long currentTime =System.currentTimeMillis();
				
		if (player.getState() != Player.State.Fire && currentTime -player.getStartTime() >200 &&player.currentLevel>=Player.LEVEL_FIRE) {
			world.creatNewBullet(player.getPosition().x + player.getWidth(),
					player.getPosition().y + player.getHeight(),
					player.isFacingRight());
			
			player.setLastState(player.getState());
			player.setState(Player.State.Fire);
			player.setStartTime(System.currentTimeMillis());
			world.playFireBallSound();
			
		}
	}

	public void leftReleased() {
		if (!Player.State.Jumping.equals(player.getState())
				&& !Player.State.Growing.equals(player.getState())) {
			player.setState(Player.State.Walking);
		}
		
		player.setFacingRight(false);
		player.getAcceleration().x -= ACCELERATION;
	}

	public void jumpPressed() {
		if (player.getState() != State.Died && player.getState()!=State.Automatic_walking) {
			if (player.canJump) {
				player.setState(Player.State.Jumping);
				player.getAcceleration().y += 2000;
				player.canJump = false;
				// jumpSound.play();
				world.playJumpSound();
			}
			holdingKey = keyPool.obtain();
			holdingKey.keyCode = Keys.L;
			holdingKey.time = System.currentTimeMillis();
		}
	}

	public void jumpReleased() {
		holdingKey = null;
	}

	public void fireReleased() {
	}

	public void update(float delta) {
		processInput();

	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {

		Camera camera = scr.getRenderer().getCamera();
		Vector3 vector3 = vector3_pool.obtain();
		vector3.x = x;
		vector3.y = y;
		vector3 = camera.unproject(vector3);
		if (world.getFireArrow().contains(vector3.x, vector3.y)) {
			firePressed();
		}
		else if (world.getLeftArrow().contains(vector3.x, vector3.y)) {
			// leftPressed();
			isTouchLeft = true;
		} else if (world.getRightArrow().contains(vector3.x, vector3.y)) {
			// rightPressed();
			isTouchRight = true;
		} else if (world.getJumpArrow().contains(vector3.x, vector3.y)) {
			jumpPressed();
		}

		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		Camera camera = scr.getRenderer().getCamera();
		Vector3 vector3 = vector3_pool.obtain();
		vector3.x = x;
		vector3.y = y;
		vector3 = camera.unproject(vector3);

		if (world.getLeftArrow().contains(vector3.x, vector3.y)) {
			// leftPressed();
			isTouchLeft = false;
		}
		if (world.getRightArrow().contains(vector3.x, vector3.y)) {
			// rightPressed();
			isTouchRight = false;
		}

		if (world.getJumpArrow().contains(vector3.x, vector3.y)) {
			jumpReleased();
		}

		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.L) {
			jumpPressed();
		}

		if (keycode == Keys.K) {
			firePressed();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.L) {
			jumpReleased();
		}
		return true;
	}

	private boolean processInput() {
		if (player.getState() == Player.State.Growing
				|| player.getState() == State.Died
				|| player.getState() == State.Injuried
				|| player.getState() == State.pulled_flag
				|| player.getState() == State.Automatic_walking) {
			return true;
		}
			if (Gdx.input.isKeyPressed(Keys.Z) || isTouchLeft) {
				leftPressed();
			} else if (Gdx.input.isKeyPressed(Keys.C)
					|| Gdx.input.isKeyPressed(Keys.DEL) || isTouchRight) {
				rightPressed();
			} else {
				if (!Player.State.Jumping.equals(player.getState())
						&& !Player.State.Growing.equals(player.getState())) {
					player.changeFrameDuration(0.1f);
					// player.setState(Player.State.Standing);
				}
				player.getAcceleration().x = 0;
			}
			if (holdingKey != null) {
				player.getAcceleration().y += 90;
			}
		return true;

	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	class HoldingKey {
		int keyCode;
		long time;
	}

}
