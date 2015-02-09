package com.baboviolent.game;

import com.baboviolent.game.screen.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class BaboViolentGame extends Game {
	static public String DESKTOP_PREFIX = "./bin/";
	static public String PATH_TEXTURE_OTHERS = "data/texture/other/";
	static public String PATH_TEXTURE = "data/texture/";
    static public String PATH_TEXTURE_GROUND = "data/texture/ground/";
    static public String PATH_TEXTURE_WALL = "data/texture/wall/";
    static public String PATH_TEXTURE_GROUND_OPTIMIZED = "data/texture/ground/optimized/";
    static public String PATH_TEXTURE_EXTERNAL_OPTIMIZED = "babo/maps/optimized/textures/";
    static public String PATH_MODELS = "data/models/";
    static public String PATH_PARTICLES = "data/particles/";
    static public String PATH_EDITOR = "data/editor/";
    static public String PATH_MAPS = "data/maps/";
    static public String PATH_MAPS_EXTERNAL = "babo/maps/";
    static public String PATH_MAPS_EXTERNAL_OPTIMIZED = "babo/maps/optimized/";
    static public String PATH_SOUNDS = "data/sounds/";
    static public String PATH_SHADERS = "data/shaders/";
    static public String BABO_MODEL_NAME = "babo";
    static public String EXTENSION_MAP = "babomap";
    static public final float SIZE_MAP_CELL = 2;
    static public final float BABO_DIAMETER = 1;
	
	@Override
	public void create () {
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	public void dispose() {
    }
	
	static public boolean isMobile() {
		switch(Gdx.app.getType()) {
			case Android:
			case iOS:
				return true;
			default:
		}
		
		return false;
	}
}
