package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Configuration.ConfigurationAdapter;
import com.baboviolent.game.Utils;
import com.baboviolent.game.batch.BaboModelBatch;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.mode.BaseMode;
import com.baboviolent.game.mode.DeathMatchMode;
import com.baboviolent.game.mode.DeathMatchMultiplayerMode;
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
	private BaboModelBatch modelBatch;
	private DecalBatch decalBatch;
	private BaboModelBatch shadowBatch;
	private BaseMode mode;
	private BulletContactListener bulletContactListener;
	private FPSLogger fps;
	private RenderContext renderContext;
	
	public GameScreen(final BaboViolentGame g, int type) {
		if( BaboViolentGame.DEBUG ) {
			GLProfiler.enable();
			fps = new FPSLogger();
		}
		
		Bullet.init();
		game = g;
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 1));
		modelBatch = new BaboModelBatch(renderContext);
		shadowBatch = new BaboModelBatch(renderContext, new DepthShaderProvider());
		
		/*modelBatch = new ModelBatch();
		shadowBatch = new ModelBatch(new DepthShaderProvider());*/
		
		// Gestion des preferences
		Preferences prefs = Gdx.app.getPreferences("com.baboviolent.game");
        prefs.putString("username", Utils.getRandomUsername());
        prefs.flush();
        
		// Initialisation du mode
        if( type == TYPE_SOLO ) {
        	mode = new DeathMatchMode("test");
        }
        else if ( type == TYPE_MULTIPLAYER ) {
        	mode = new DeathMatchMultiplayerMode("test");
        }
        mode.init();
		
		// On cree le contact listener de bullet
		bulletContactListener = new BulletContactListener();
		
		decalBatch = new DecalBatch(new CameraGroupStrategy(mode.getCamera()));
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mode.update();
		//configAdapter.update();
		
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		//renderContext.begin();
		mode.render(renderContext, modelBatch, shadowBatch, decalBatch);
		//renderContext.end();
		
		// La mise a jour du controleur doit etre apres le rendu
		// car affichage des touchpad
		mode.getController().update();
		
		if( BaboViolentGame.DEBUG ) {
			fps.log();
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
		modelBatch.dispose();
		modelBatch = null;
	}
}
