package com.mario.model;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface IEntity {
	public void loadTexture();

	public void draw(Batch b);

	public void update(float delta);
}
