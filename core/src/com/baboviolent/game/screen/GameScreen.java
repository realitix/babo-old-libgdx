package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.camera.ChaseCamera;
import com.baboviolent.game.camera.ChaseCamera2;
import com.baboviolent.game.controller.BaboController;
import com.baboviolent.game.controller.DesktopController;
import com.baboviolent.game.effect.particle.PoolParticle;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.weapon.Shotgun;
import com.baboviolent.game.loader.BaboModelLoader;
import com.baboviolent.game.loader.ParticleLoader;
import com.baboviolent.game.mode.BaseMode;
import com.baboviolent.game.mode.DeathMatchMode;
import com.baboviolent.game.mode.DeathMatchMultiplayerMode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class GameScreen implements Screen {
	public final static int TYPE_SOLO = 1;
	public final static int TYPE_MULTIPLAYER = 2;
	
	final private BaboViolentGame game;
	private ModelBatch modelBatch;
	private DecalBatch decalBatch;
	private BaseMode mode;
	private BulletContactListener bulletContactListener;
	
	public GameScreen(final BaboViolentGame g, int type) {
		Bullet.init();
		game = g;
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		modelBatch = new ModelBatch();
		
		// Gestion des préférences
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
		mode.update();
		
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		mode.render(modelBatch, decalBatch);
		
		// La mise a jour du controleur doit etre apres le rendu
		// car affichage des touchpad
		mode.getController().update();
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
