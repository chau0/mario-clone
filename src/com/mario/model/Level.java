package com.mario.model;

import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader.AtlasTiledMapLoaderParameters;

public class Level {
	private TiledMap map;
	private float tileWidth;
	private float tileHeight;
	TiledMapTileLayer layer;

	public Level(String tilemapName) {
		AtlasTiledMapLoaderParameters parameters = new AtlasTiledMapLoaderParameters();
		map = new AtlasTmxMapLoader().load("map_1_1.tmx", parameters);
		// map = new TmxMapLoader().load(tilemapName);
		layer = (TiledMapTileLayer) map.getLayers().get(0);
		tileWidth = layer.getTileWidth();
		tileHeight = layer.getTileHeight();

	}

	public TiledMap getMap() {
		return map;
	}

	public float getTileHeight() {
		return tileHeight;
	}

	public float getTileWidth() {
		return tileWidth;
	}

	public TiledMapTileLayer getLayer() {
		return layer;
	}
	
	public void dispose(){
		map.dispose();
		layer=null;
	}
}
