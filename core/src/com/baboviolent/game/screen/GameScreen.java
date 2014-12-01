package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.camera.ChaseCamera;
import com.baboviolent.game.camera.ChaseCamera2;
import com.baboviolent.game.controller.DesktopController;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.weapon.Shotgun;
import com.baboviolent.game.loader.BaboModelLoader;
import com.baboviolent.game.mode.BaseMode;
import com.baboviolent.game.mode.DeathMatchMode;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
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
	final private BaboViolentGame game;
	private Environment environment;
	private DirectionalLight light;
	private BulletWorld world;
	private ModelBuilder modelBuilder = new ModelBuilder();
	private ModelBatch modelBatch;
	private Array<Disposable> disposables = new Array<Disposable>();
	private ChaseCamera2 camera;
	private AssetManager assets;
	private boolean loading;
	private BaseMode mode;
	private Array<Babo> babos = new Array<Babo>();
	private Babo player;
	private DesktopController controller;
	private BulletContactListener bulletContactListener;
	
	public GameScreen(final BaboViolentGame g) {
		Bullet.init();
		game = g;
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
		light = new DirectionalLight();
		light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);
		environment.add(light);
		modelBatch = new ModelBatch();
		
		// Initialisation du monde
		world = new BulletWorld();
		
		// Initialisation du mode
		mode = new DeathMatchMode("test");
		world.add(mode.getMapInstance());
		
		 // Initialisation du joueur
        player = (Babo) new Babo("skin22").translate(new Vector3(800, 20, 600));
        world.add(player);
        babos.add(player);
        
        // Initialisation de l'arme
        Shotgun shotgun = new Shotgun(world);
        world.attachWeaponToBabo(player, shotgun);
		
		// Initialisation de la caméra
		camera = new ChaseCamera2(player);
		
		// Initialisation du controller
		controller = new DesktopController(this, player);
		Gdx.input.setInputProcessor(controller);
		
		// On créé le contact listener de bullet
		bulletContactListener = new BulletContactListener(babos);
    }
    
    public void mouseMoved(int screenX, int screenY) {
    	Vector3 position = getPositionFromMouse(screenX, screenY);
    	player.setTarget(position);
    }
    
    public void mouseClicked(int screenX, int screenY) {
    	player.shoot();
    }
    
    public void mouseReleased(int screenX, int screenY) {
    	player.stopShoot();
    }
    
    /**
	 * Renvoie la position sur la grille en fonction de la souris
	 */ 
	private Vector3 getPositionFromMouse(int screenX, int screenY) {
		Vector3 position = new Vector3();
		Ray ray = camera.getPickRay(screenX, screenY);
        final float distance = -ray.origin.y / ray.direction.y;
        position.set(ray.direction).scl(distance).add(ray.origin);
        return position;
	}

	private Vector3 getPositionFromMouse() {
		return getPositionFromMouse(Gdx.input.getX(), Gdx.input.getY());
	}
	
	@Override
	public void render(float delta) {
		update();
		beginRender();
		renderWorld();
		/*Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);*/		
	}
	
	private void beginRender() {
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}
	
	private void renderWorld () {
		modelBatch.begin(camera);
		world.render(modelBatch, environment);
		modelBatch.end();
	}
	
	private void update () {
		camera.update();
		world.update();
		player.update(getPositionFromMouse());
		
		// La mise à jour du controller doit absolument etre faite
		// après la mise à jour du monde bullet
		//controller.update();
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
		world.dispose();
		world = null;

		for (Disposable disposable : disposables)
			disposable.dispose();
		disposables.clear();

		modelBatch.dispose();
		modelBatch = null;

		light = null;
	}
}
