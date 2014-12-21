package com.baboviolent.game;

import com.baboviolent.game.screen.GameScreen;
import com.baboviolent.game.screen.MainMenuScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class BaboViolentGame extends Game {
	static public String DESKTOP_PREFIX = "./bin/";
	static public String PATH_TEXTURE = "data/texture/";
    static public String PATH_TEXTURE_GROUND = "data/texture/ground/";
    static public String PATH_TEXTURE_WALL = "data/texture/wall/";
    static public String PATH_MODELS = "data/models/";
    static public String PATH_PARTICLES = "data/particles/";
    static public String PATH_MAPS = "data/maps/";
    static public String BABO_MODEL_NAME = "babo";
    static public final float SIZE_MAP_CELL = 200;
    static public final float BABO_DIAMETER = 100;
    static public final float BABO_SPEED = 10;
	public SpriteBatch batch;
	public BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	public void dispose() {
        batch.dispose();
    }
	
	/**
	 * Renvoie le chemin en fonction de la plateforme
	 * @param path
	 * @return path
	 */
	public static String path(String path) {
		switch(Gdx.app.getType()) {
			case Desktop:
				return DESKTOP_PREFIX+path;
			default:
				break;
		}
		
		return path;
	}
}
