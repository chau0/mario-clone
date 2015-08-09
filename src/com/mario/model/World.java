package com.mario.model;

import java.util.Iterator;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mario.model.Block.BLOCK_TYPE;
import com.mario.model.Player.State;
import com.mario.screen.GameScreen;
import com.mario.util.Constant;
import com.mario.util.Debug;
import com.mario.util.TileId;

public class World {
	private String blockedKey = "blocked";
	private Level level;
	private Player player;
	// private Vector2 playerStartPos = new Vector2(13f, 3f);
	private Rectangle leftArrow;
	private Rectangle rightArrow;
	private Rectangle jumpArrow;
	private Rectangle fireArrow;

	private TiledMapTileLayer layer;
	private int[][] mapBlocks;
	private Array<Block> listBlocks;
	private Array<Goomba> listGoombas;
	private Array<BrickBroken> listBrickBrokens;
	private Array<Duck> listDucks;
	private int layerWidth;
	private int layerHeight;
	private int[] replacementTileId = { TileId.BRICK_BLOCK_1_1,
			TileId.BRICK_BLOCK_1_2, TileId.BRICK_BLOCK_1_3,
			TileId.BRICK_BLOCK_1_4, TileId.BRICK_BLOCK_2_1,
			TileId.BRICK_BLOCK_2_2, TileId.BRICK_BLOCK_2_3,
			TileId.BRICK_BLOCK_2_4, TileId.COIN_BLOCK_1, TileId.COIN_BLOCK_2,
			TileId.COIN_BLOCK_3, TileId.COIN_BLOCK_4, TileId.GOOM_BA_1,
			TileId.GOOM_BA_2, TileId.GOOM_BA_3, TileId.GOOM_BA_4,
			TileId.DUCK_1, TileId.DUCK_2, TileId.DUCK_3, TileId.DUCK_4,
			TileId.DUCK_5

	};

	private MushRoom mushRoom;
	private Flower flower;
	private Music themeMusic;
	private Sound stageClearSound;
	private Sound bumpSound;
	private Sound powerUpSound;
	private Sound powerUpAppearSound;
	private Sound coinSound;
	private Sound jumpSound;
	private Sound breakBlockSound;
	private Sound fireBallSound;
	private Sound marioKickSound;
	private Sound flagPoleSound;
	private Sound marioDieSound;
	private Sound powerDownSound;
	private Array<Bullet> listBullets;
	private Flag flag;
	private long timeWaitReset =-1;
	public boolean isWorldReset =false;
	Pool<Bullet> poolBullet = new Pool<Bullet>() {

		@Override
		protected Bullet newObject() {
			return new Bullet();
		}
	};
	
	private GameScreen gameScreem;

	public World(GameScreen screen) {
        this.gameScreem =screen;
		formatTile();
		player = new Player(this);
		layer = level.getLayer();
		listBrickBrokens = new Array<BrickBroken>();
		listBullets = new Array<Bullet>();
		initArrow();
		player.stopHorizontal();
		player.stopVertical();
		themeMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/theme.mp3"));
		stageClearSound = Gdx.audio.newSound(Gdx.files.internal("sound/stage_clear.wav"));
		bumpSound = Gdx.audio.newSound(Gdx.files.internal("sound/bump.wav"));
		coinSound = Gdx.audio.newSound(Gdx.files.internal("sound/coin.wav"));
		powerUpAppearSound = Gdx.audio.newSound(Gdx.files
				.internal("sound/powerup_appear.wav"));
		powerUpSound = Gdx.audio.newSound(Gdx.files
				.internal("sound/powerup.wav"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("sound/jump.wav"));
		breakBlockSound = Gdx.audio.newSound(Gdx.files
				.internal("sound/breakblock.wav"));
		fireBallSound = Gdx.audio.newSound(Gdx.files
				.internal("sound/fireball.wav"));
		marioKickSound =Gdx.audio.newSound(Gdx.files
				.internal("sound/kick.wav"));
		flagPoleSound =Gdx.audio.newSound(Gdx.files
				.internal("sound/flagpole.wav"));
		
		marioDieSound =Gdx.audio.newSound(Gdx.files
				.internal("sound/mariodie.wav"));
		powerDownSound =Gdx.audio.newSound(Gdx.files
				.internal("sound/power_down.wav"));
		themeMusic.play();
		
	}

	public void formatTile() {
		level = new Level("map_test.tmx");
		listGoombas = new Array<Goomba>();
		listBlocks = new Array<Block>();
		listDucks = new Array<Duck>();
		TiledMap map = level.getMap();
		TiledMapTile skyTile = map.getTileSets().getTile(TileId.SKY);
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
				"Ground");
		layerWidth = layer.getWidth();
		layerHeight = layer.getHeight();
		mapBlocks = new int[layerWidth][layerHeight];
		// special block

		// mushroom
		layer.getCell(42, 9).setTile(skyTile);
		creatNewBlock(42, 9, BLOCK_TYPE.MUSH_ROOM_COIN, 0);
		
		layer.getCell(156, 9).setTile(skyTile);
		creatNewBlock(156, 9, BLOCK_TYPE.MUSH_ROOM_COIN, 0);

		for (int x = 0; x < 400; x++) {
			for (int y = 0; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
				try {
					int tileId = cell.getTile().getId();
					if (isReplacementTile(tileId)) {
						cell.setTile(skyTile);
						if (tileId == TileId.BRICK_BLOCK_1_1
								|| tileId == TileId.BRICK_BLOCK_2_1) {
							if (x == 0 || mapBlocks[x][y] == 0) {
								creatNewBlock(x, y, BLOCK_TYPE.BRICK, tileId);
							}

						} else if (tileId == TileId.COIN_BLOCK_1) {
							creatNewBlock(x, y, BLOCK_TYPE.COIN, tileId);
						} else if (tileId == TileId.GOOM_BA_1) {
							creatNewGoomBa(x, y);
						} else if (tileId == TileId.DUCK_1) {
							createNewDuck(x, y);
						} 
					}
					if (tileId == TileId.FLAG_HEAD) {
						Debug.d("flag :" + x + "," + y);
						createNewFlag(x-2, y-2.2f);
					}
				} catch (NullPointerException e) {
					Debug.d("error :" + x + "," + y);
					e.printStackTrace();
				}
				
			}
		}

	}
	
	private void createNewFlag(float x,float y)
	{
		flag =new Flag();
		flag.init(this, x, y);
	   
	}

	private void createNewDuck(int x, int y) {
		Duck duck = new Duck();
		duck.init(this, x, y);
		listDucks.add(duck);
	}

	private void creatNewGoomBa(int x, int y) {
		Goomba goomba = new Goomba();
		goomba.init(this, x, y);
		listGoombas.add(goomba);
	}

	private void creatNewBlock(int x, int y, BLOCK_TYPE type, int tileId) {
		Block block = new Block(this, x, y, type, tileId);
		listBlocks.add(block);
		int currentIndex = listBlocks.size;
		mapBlocks[x][y] = currentIndex;
		mapBlocks[x][y + 1] = currentIndex;
		mapBlocks[x + 1][y] = currentIndex;
		mapBlocks[x + 1][y + 1] = currentIndex;
	}

	private boolean isReplacementTile(int id) {
		for (int i = 0; i < replacementTileId.length; i++) {
			if (id == replacementTileId[i]) {
				return true;
			}
		}

		return false;
	}

	private void initArrow() {
		leftArrow = new Rectangle(1, 0.5f, 4, 4);
		rightArrow = new Rectangle(4, 0.5f, 4, 4);
		jumpArrow = new Rectangle(20, 0.5f, 4, 4);
		fireArrow = new Rectangle(20, 0.5f, 4, 4);
	}

	public void draw(Batch b, float delta, float cameraX, float cameraWidth) {
		if(isWorldReset){
			return;
		}
		if (flag != null) {
			flag.draw(b);
		}
		player.draw(b, delta);
		if (mushRoom != null) {
			mushRoom.draw(b);
		}
		// flower
		if (flower != null) {
			flower.draw(b);
		}
		for (Block block : listBlocks) {
			block.draw(b, delta);
		}

		if (listBrickBrokens.size > 0) {
			Iterator<BrickBroken> it = listBrickBrokens.iterator();
			while (it.hasNext()) {
				BrickBroken brickBroken = it.next();
				brickBroken.draw(b);
			}

		}

		if (listBullets.size > 0) {
			Iterator<Bullet> it = listBullets.iterator();
			while (it.hasNext()) {
				Bullet bullet = it.next();
				bullet.draw(b);
			}
		}

		if (listGoombas.size > 0) {
			Iterator<Goomba> it = listGoombas.iterator();
			while (it.hasNext()) {
				Goomba goomba = it.next();
				goomba.draw(b);
			}
		}

		if (listDucks.size > 0) {
			Iterator<Duck> it = listDucks.iterator();
			while (it.hasNext()) {
				Duck duck = it.next();
				duck.draw(b);
			}
		}
		
		

	}

	public void update(float delta, float cameraX, float cameraWidth) {
		if(isWorldReset){
			return;
		}
		// player
		player.update(delta, cameraX, cameraWidth);
		// mushroom
		if (mushRoom != null) {
			mushRoom.update(delta);
			if (mushRoom.position.x + mushRoom.width < cameraX - cameraWidth
					/ 2) {
				mushRoom = null;
			} else if (player.checkCollidesWithEntity(mushRoom.position.x,
					mushRoom.position.y, mushRoom.width, mushRoom.height)) {
				player.levelUp();
				mushRoom = null;
			}
		}

		// flower
		if (flower != null) {
			flower.update(delta);

			if (player.checkCollidesWithEntity(flower.position.x,
					flower.position.y, flower.width, flower.height)) {
				player.levelUp();

				flower = null;

			}
		}
		// block
		for (Block block : listBlocks) {
			block.update(delta);
		}

		if (listBullets.size > 0) {
			Iterator<Bullet> it = listBullets.iterator();
			while (it.hasNext()) {
				Bullet bullet = it.next();
				bullet.update(delta);
				if (bullet.isRemove) {
					it.remove();
				}
			}
		}

		if (listBrickBrokens.size > 0) {
			Iterator<BrickBroken> it = listBrickBrokens.iterator();
			while (it.hasNext()) {
				BrickBroken brickBroken = it.next();
				brickBroken.update(delta);
				if (brickBroken.isRemove) {
					it.remove();
				}
			}

		}

		if (listGoombas.size > 0 && player.getState() != State.Injuried
				&& player.getState() != State.Died) {
			Iterator<Goomba> it = listGoombas.iterator();
			boolean isKickGoomba =false;
			while (it.hasNext()) {
				Goomba goomba = it.next();
				if (cameraX + cameraWidth + 2 > goomba.position.x) {
					goomba.update(delta);
					if (goomba.isRemove) {
						it.remove();
					} else if (!goomba.isDied && !goomba.isRotate) {
						if (player.checkCollidesWithEntity(goomba.position.x,
								goomba.position.y, goomba.width, goomba.height)) {
							if (player.getVelocity().y < 0
									&& player.getPosition().y > goomba.position.y
											+ goomba.width / 2) {
								goomba.killed(true);
								isKickGoomba =true;
								playMarioKickSound();
							} else {
								player.injuried();
							}
						}
					}
				}
				if (!goomba.isRotate) {

					if (listBullets.size > 0) {
						Iterator<Bullet> bulletIt = listBullets.iterator();
						while (bulletIt.hasNext()) {
							Bullet bullet = bulletIt.next();
							if (bullet.checkCollides(goomba)) {
								goomba.killed(false);
								bullet.fired();
							}
						}
					}
				}

			}
			if(isKickGoomba){
				player.jump();
			}
		}

		if (listDucks.size > 0) {
			Iterator<Duck> it = listDucks.iterator();
			while (it.hasNext()) {
				Duck duck = it.next();
				if (cameraX + cameraWidth + 2 > duck.position.x) {
					duck.update(delta);
				}

				if (duck.isRemove) {
					it.remove();
				} else if (player.getState() != State.Injuried
						&& player.getState() != State.Died) {
					if (player.checkCollidesWithEntity(duck.position.x,
							duck.position.y, duck.width, duck.height)) {
						if (duck.state == Duck.State.noHead) {
							duck.velocity.x = player.isFacingRight() ? 0.5f
									: -0.5f;
						} else if (player.getVelocity().y < 0
								&& player.getPosition().y > duck.position.y
										+ duck.width) {
							duck.injuried();
							player.jump();
							playMarioKickSound();
						} else {
							player.injuried();
						}
					} else if (duck.state == Duck.State.noHead
							&& Math.abs(duck.velocity.x) > 0) {
						if (duck.position.x > cameraWidth/2 + cameraX
								|| duck.position.x + duck.width < cameraX -cameraWidth/2) {
                            duck.isRemove =true;
						} else if (listGoombas.size > 0) {
							Iterator<Goomba> goombaIt = listGoombas.iterator();
							while (goombaIt.hasNext()) {
								Goomba goomba = goombaIt.next();
								if (goomba.checkCollides(duck)) {
									goomba.killed(false);
									playMarioKickSound();
								}
							}
						}
					}
					
					if(listBullets.size>0 &&!duck.isRotate)
					{
						Iterator<Bullet> bulletIt = listBullets.iterator();
						while (bulletIt.hasNext()) {
							Bullet bullet = bulletIt.next();
							if (bullet.checkCollides(duck)) {
								duck.killed(player.isFacingRight());
								playMarioKickSound();
								bullet.fired();
							}
						}
					}
				}
			}
		}
		if (flag != null)
			flag.update(delta);
		
		if(timeWaitReset>0 && System.currentTimeMillis() -timeWaitReset>5000){
			reset();
		}

	}

	public void setPlayerSize(float width, float height) {
		player.setWidth(width);
		player.setHeight(height);
	}

	public Level getLevel() {
		return level;
	}

	public Player getPlayer() {
		return player;
	}

	public void renderArrow(ShapeRenderer shapeRenderer, float startX,
			float endX) {
		leftArrow.x = startX;
		rightArrow.x = leftArrow.x + 4;
		jumpArrow.x = endX - 4;
		fireArrow.x = endX - 8;
		shapeRenderer.triangle(leftArrow.x + 1, leftArrow.y + leftArrow.height
				/ 4, leftArrow.x + leftArrow.width / 2 + 1, leftArrow.y,
				leftArrow.x + leftArrow.width / 2 + 1, leftArrow.y
						+ leftArrow.height / 2);

		shapeRenderer.triangle(rightArrow.x + 1, rightArrow.y,
				rightArrow.x + 1, rightArrow.y + rightArrow.height / 2,
				rightArrow.x + rightArrow.width / 2 + 1, rightArrow.y
						+ rightArrow.height / 4);

		shapeRenderer.rect(jumpArrow.x, jumpArrow.y, 2, 2);

		shapeRenderer.rect(fireArrow.x, fireArrow.y, 2, 2);
	}

	public void hideBlock(Block block) {
		block.isHidden = true;
		int x = (int) block.anchorPoint.x;
		int y = (int) block.anchorPoint.y;
		mapBlocks[x][y] = 0;
		mapBlocks[x][y + 1] = 0;
		mapBlocks[x + 1][y] = 0;
		mapBlocks[x + 1][y + 1] = 0;

		float speedX = 10;
		float speedY = 45;
		BrickBroken bb1 = new BrickBroken(new Vector2(
				block.anchorPoint.x - 0.5f, block.anchorPoint.y - 1),
				new Vector2(-speedX, speedY - 5));
		BrickBroken bb2 = new BrickBroken(
				new Vector2(block.anchorPoint.x - 0.5f, block.anchorPoint.y
						+ block.height), new Vector2(-speedX, speedY));
		BrickBroken bb3 = new BrickBroken(new Vector2(block.anchorPoint.x
				+ block.width - 0.5f, block.anchorPoint.y - 1), new Vector2(
				speedX, speedY - 5));
		BrickBroken bb4 = new BrickBroken(new Vector2(block.anchorPoint.x
				+ block.width - 0.5f, block.anchorPoint.y + block.height),
				new Vector2(speedX, speedY));
		listBrickBrokens.add(bb1);
		listBrickBrokens.add(bb2);
		listBrickBrokens.add(bb3);
		listBrickBrokens.add(bb4);
		playBreakBlockSound();

	}

	public boolean collidesBottom(Vector2 position, Vector2 velocity,
			float width, float height, boolean isPrint) {
		float newPositionY = position.y + velocity.y;
		float endX = width;
		float minDeltaX = 0;
		float maxDeltaX = 0;
		for (float step = 0; step <= endX; step += 0.5f) {
			int blockId = checkCollidesBlock(position.x + step, newPositionY);
			if (blockId > 0) {
				Block block = listBlocks.get(blockId - 1);
				float deltaX = block.position.x - position.x - width - 0.2f;
				if (Math.abs(deltaX) < 0.7f && deltaX != 0 && velocity.x == 0) {
					minDeltaX = Math.min(deltaX, minDeltaX);
				} else {
					deltaX = block.position.x + block.width - position.x + 0.2f;

					if (Math.abs(deltaX) < 0.8f && deltaX != 0
							&& velocity.x == 0) {
						maxDeltaX = Math.max(deltaX, maxDeltaX);
					} else {
						return true;
					}
				}
			}
			if (isCellBlocked(position.x + step, newPositionY)) {
				float deltaX = (position.x + step) - position.x - width - 0.2f;
				if (Math.abs(deltaX) < 0.7f && deltaX != 0) {
					minDeltaX = Math.min(deltaX, minDeltaX);
				} else {
					deltaX = (int) (position.x + step) + layer.getTileWidth()
							* Constant.UNIT_SCALE - position.x;
					if (Math.abs(deltaX) < 0.8f && deltaX != 0
							&& velocity.x == 0) {
						maxDeltaX = Math.max(deltaX, maxDeltaX);
					} else {
						return true;
					}
				}
			}
		}
		if (minDeltaX != 0) {
			velocity.x = minDeltaX;
			return false;
		}

		if (maxDeltaX != 0) {
			velocity.x = maxDeltaX;
		}
		return false;
	}

	// public boolean collidesBottom(Vector2 position, Vector2 velocity,
	// float width, float height, boolean isPrint) {
	// float newPositionY = position.y + velocity.y;
	// float endX = width;
	// for (float step = 0; step <= endX; step += 0.5f) {
	// if (checkCollidesBlock(position.x + step, newPositionY) > 0) {
	// return true;
	// }
	// if (isCellBlocked(position.x + step, newPositionY)) {
	// return true;
	// }
	//
	// }
	// return false;
	// }

	public boolean collidesTop() {
		float newPositionY = player.getPosition().y + player.deltaPos.y
				+ player.getHeight();
		boolean canBreak = player.currentLevel > Player.LEVEL_SMALL;
		float minDeltaX = 0;
		for (float step = player.getWidth(); step >= 0; step--) {
			int blockId = checkCollidesBlock(player.getPosition().x + step,
					newPositionY);
			if (blockId > 0) {
				Block block = listBlocks.get(blockId - 1);
				float deltaX = (block.position.x - player.getPosition().x - player
						.getWidth());

				if (Math.abs(deltaX) <= 0.7f) {
					minDeltaX = Math.min(deltaX, minDeltaX);
					// Debug.d("delta x  start :" + minDeltaX + "," + step);
				} else {
					deltaX = (block.position.x + block.width - player
							.getPosition().x);
					if (deltaX <= 0.7f) {
						// Debug.d("delta x  end :" + deltaX + "," + step);
						minDeltaX = Math.max(deltaX, minDeltaX);
					} else {
						if (canBreak && block.type == BLOCK_TYPE.BRICK) {
							hideBlock(block);
						} else {
							block.vibrate();
						}
						return true;
					}
				}
			}
			if (isCellBlocked(player.getPosition().x + step, newPositionY)) {
				return true;
			}
		}
		if (Math.abs(minDeltaX) > 0) {
			player.deltaPos.x = minDeltaX;
			return false;
		}

		return false;
	}

	public int checkCollidesBlock(float x, float y) {
		if (x >= 0 && x < layerWidth && y >= 0 && y < layerHeight) {
			return mapBlocks[(int) x][(int) y];

		}
		return -1;
	}

	// private boolean checkCollidesBlock(float x, float y, boolean canVibrate,
	// boolean canBrocken) {
	// if (x >= 0 && x < layerWidth && y >= 0 && y < layerHeight) {
	// int blockId = mapBlocks[(int) x][(int) y];
	// if (blockId != 0) {
	// Block block = listBlocks.get(blockId - 1);
	// if (canBrocken &&block.type ==BLOCK_TYPE.BRICK) {
	// hideBlock(block);
	// } else if (canVibrate) {
	// block.vibrate();
	// }
	// return true;
	// }
	//
	// }
	// return false;
	// }
	public void stopThemeMusic(){
		themeMusic.stop();
	}
	
	public void playerPowerDownSound(){
		powerDownSound.play();
	}
	
	public void playStageClearSound(){
		stageClearSound.play();
		timeWaitReset =System.currentTimeMillis();
	}
	public void playMarioDieSound() {
		marioDieSound.play();
	}
	public void playFlagPole() {
		flagPoleSound.play();
	}
	public void playMarioKickSound() {
		marioKickSound.play();
	}

	public void playFireBallSound() {
		fireBallSound.play();
	}

	public void playBreakBlockSound() {
		breakBlockSound.play();
	}

	public void playJumpSound() {
		jumpSound.play();
	}

	public void playPowerUp() {
		powerUpSound.play();
	}

	public void playPowerUpAppear() {
		powerUpAppearSound.play();
	}

	public void playCoinSound() {
		coinSound.play();
	}

	public void playBumpSound() {
		bumpSound.play();
	}

	public Array<Block> getBlockCollided(Vector2 position, Vector2 velocity,
			float width, float height) {
		float newPositionY = position.y + velocity.y;
		Array<Block> listCollides = new Array<Block>();
		for (float step = width; step >= 0; step--) {
			float x = position.x + step;
			float y = newPositionY;

			if (x < layerWidth && y < layerHeight) {
				int blockId = mapBlocks[(int) x][(int) y];
				if (blockId != 0) {
					Block block = listBlocks.get(blockId - 1);
					if (block.isCollidesWithEntity(x, y, width, height)) {
						listCollides.add(block);
					}

				}
			}
		}

		return listCollides;
	}

	public void creatNewBullet(float x, float y, boolean isFacingRight) {
		Bullet bullet = poolBullet.obtain();
		bullet.init(this, x - 1, y - 1.5f, isFacingRight);
		listBullets.add(bullet);
	}

	public void creatNewSpecialItem(float x, float y) {
		if (player.currentLevel == Player.LEVEL_SMALL) {
			creatNewMushRoom(x, y);
		} else if (player.currentLevel == Player.LEVEL_BIG) {

			creatNewFlower(x, y);
		}
	}

	public void creatNewMushRoom(float x, float y) {
		mushRoom = new MushRoom(this, x, y);

	}

	public void creatNewFlower(float x, float y) {
		flower = new Flower(x, y);
	}

	public boolean collidesLeft(Vector2 position, Vector2 velocity,
			float width, float height) {
		float newPositionX = position.x + velocity.x;
		for (int step = 0; step < height; step++) {
			if (isCellBlocked(newPositionX, position.y + step)) {
				return true;
			}

			if (checkCollidesBlock(newPositionX, position.y + step) > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean collidesRight(Vector2 position, Vector2 velocity,
			float width, float height) {
		float newPositionX = position.x + velocity.x + width;
		for (int step = 0; step < height; step++) {
			if (isCellBlocked(newPositionX, position.y + step)) {
				return true;
			}

			if (checkCollidesBlock(newPositionX, position.y + step) > 0) {
				return true;
			}

		}
		return false;
	}

	public boolean isCellBlocked(float x, float y) {
		Cell cell = layer.getCell((int) (x), (int) (y));
		return cell != null && cell.getTile() != null
				&& cell.getTile().getProperties().containsKey(blockedKey);
	}

	public Rectangle getLeftArrow() {
		return leftArrow;
	}

	public Rectangle getRightArrow() {
		return rightArrow;
	}

	public Rectangle getJumpArrow() {
		return jumpArrow;
	}

	public Rectangle getFireArrow() {
		return fireArrow;
	}

	public Flag getFlag() {
		return flag;
	}
	public void reset(){
		isWorldReset =true;
		themeMusic.stop();
		gameScreem.changeScreen();
	}
	
	public void dipose(){
		level.dispose();
		player.dipose();
		mushRoom=null;
	    themeMusic.dispose();
		stageClearSound.dispose();
	    bumpSound.dispose();;
		powerUpSound.dispose();
		powerUpAppearSound.dispose();
		coinSound.dispose();
		jumpSound.dispose();
		breakBlockSound.dispose();
		fireBallSound.dispose();
		marioKickSound.dispose();
		flagPoleSound.dispose();
		powerDownSound.dispose();
	}
}
