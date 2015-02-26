package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Configuration.ConfigurationAdapter;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.gdx.batch.BaboModelBatch;
import com.baboviolent.game.gdx.decal.BaboCameraGroupStrategy;
import com.baboviolent.game.gdx.decal.BaboDecalBatch;
import com.baboviolent.game.gdx.texture.BaboTextureBinder;
import com.baboviolent.game.loader.AssetConstant;
import com.baboviolent.game.loader.BaboAssetManager;
import com.baboviolent.game.mode.BaseMode;
import com.baboviolent.game.mode.DeathMatchMode;
import com.baboviolent.game.mode.DeathMatchMultiplayerMode;
import com.baboviolent.game.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.physics.bullet.Bullet;

public class GameScreen implements Screen {
	public final static int TYPE_SOLO = 1;
	public final static int TYPE_MULTIPLAYER = 2;
	
	final private BaboViolentGame game;
	private BaseMode mode;
	private BulletContactListener bulletContactListener;
	private FPSLogger fps;
	private boolean loaded = false;
	
	public GameScreen(final BaboViolentGame g, int type) {
		if( BaboViolentGame.DEBUG_OPENGL )
			GLProfiler.enable();
		if( BaboViolentGame.DEBUG )
			fps = new FPSLogger();
		
		Bullet.init();
		game = g;
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// Initialisation du mode
        if( type == TYPE_SOLO ) {
        	mode = new DeathMatchMode("test");
        }
        else if ( type == TYPE_MULTIPLAYER ) {
        	mode = new DeathMatchMultiplayerMode("test");
        }
       
        // Gestion des preferences
		Preferences prefs = Gdx.app.getPreferences("com.baboviolent.game");
        prefs.putString("username", Utils.getRandomUsername());
        prefs.flush();
		
		// On cree le contact listener de bullet
		bulletContactListener = new BulletContactListener();
		
		// On lance le chargement des assets
		
		// Modeles
		for( int i = 0; i < AssetConstant.models.length; i++ ) {
			BaboAssetManager.loadModel(AssetConstant.models[i]);
		}
		// Atlas
		for( int i = 0; i < AssetConstant.atlas.length; i++ ) {
			BaboAssetManager.loadAtlas(AssetConstant.atlas[i]);
		}
		// Skin
		for( int i = 0; i < AssetConstant.skins.length; i++ ) {
			BaboAssetManager.loadSkin(AssetConstant.skins[i], "game");
		}
	}
	
	private void doneLoading() {
		mode.init();
		loaded = true;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// Si pas charge
		if( !loaded ) {
			// Chargement termine
			if( BaboAssetManager.update() ) {
				doneLoading();
			}
			return;
		}
		
		mode.update();
		//configAdapter.update();
		
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		mode.render();
		
		if( BaboViolentGame.DEBUG )
			fps.log();
		if( BaboViolentGame.DEBUG_OPENGL ) {
			System.out.println("GL calls: "+GLProfiler.calls+ ", draw call: "+GLProfiler.drawCalls+", shader switch: "+GLProfiler.shaderSwitches+", texture binding: "+GLProfiler.textureBindings+", vertex: "+GLProfiler.vertexCount.total);
			GLProfiler.reset();
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		bulletContactListener.dispose();
	}
}
