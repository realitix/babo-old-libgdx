package com.baboviolent.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BaboAssetManager {
	public static final String ATLAS_PATH = "data/atlas/";
	public static final String ATLAS_EXTENSION = ".atlas";
	public static final String MODELS_PATH = "data/models/";
	public static final String MODELS_EXTENSION = ".g3dj";
	public static final String SKINS_PATH = "data/skin/";
	public static final String SKINS_EXTENSION = ".json";
	
	static private AssetManager assetManager = null;
	
	static public AssetManager getInstance() {
		if( assetManager == null ) {
			assetManager = new AssetManager();
		}
		return assetManager;
	}
	
	static public boolean update() {
		return getInstance().update();
	}
	
	static public void loadModel(String modelName) {
		getInstance().load(MODELS_PATH+modelName+MODELS_EXTENSION, Model.class);
	}
	
	static public void loadAtlas(String atlasName) {
		getInstance().load(ATLAS_PATH+atlasName+ATLAS_EXTENSION, TextureAtlas.class);
	}
	
	static public void loadSkin(String skinName) {
		getInstance().load(SKINS_PATH+skinName+SKINS_EXTENSION, Skin.class);
	}
	
	static public void loadSkin(String skinName, String atlasName) {
		SkinParameter param = new SkinParameter(ATLAS_PATH+atlasName+ATLAS_EXTENSION);
		getInstance().load(SKINS_PATH+skinName+SKINS_EXTENSION, Skin.class, param);
	}
	
	static public Model getModel(String modelName) {
		String p = MODELS_PATH+modelName+MODELS_EXTENSION;
		if( !getInstance().isLoaded(p) ) return null;
		return getInstance().get(p, Model.class);
	}
	
	static public TextureAtlas getAtlas(String atlasName) {
		String p = ATLAS_PATH+atlasName+ATLAS_EXTENSION;
		if( !getInstance().isLoaded(p) ) return null;
		return getInstance().get(p, TextureAtlas.class);
	}
	
	static public Skin getSkin(String skinName) {
		String p = SKINS_PATH+skinName+SKINS_EXTENSION;
		if( !getInstance().isLoaded(p) ) return null;
		return getInstance().get(p, Skin.class);
	}
}
