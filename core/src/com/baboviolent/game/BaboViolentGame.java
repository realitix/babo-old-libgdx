package com.baboviolent.game;

import com.baboviolent.game.screen.MainMenuScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class BaboViolentGame extends Game {
	static public String DESKTOP_PREFIX = "./bin/";
    static public String PATH_TEXTURE_GROUND = "data/texture/ground/";
    static public String PATH_TEXTURE_GROUND_DESKTOP = DESKTOP_PREFIX+PATH_TEXTURE_GROUND;
    static public String PATH_MAPS = "data/maps/";
    static public final float SIZE_MAP_CELL = 30;
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
}
