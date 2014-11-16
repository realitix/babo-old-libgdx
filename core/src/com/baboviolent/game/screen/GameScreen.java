package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletConstructor;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.loader.MapLoader;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class GameScreen implements Screen {
	final BaboViolentGame game;
	public Environment environment;
	public DirectionalLight light;
	public BulletWorld world;
	public ModelBuilder modelBuilder = new ModelBuilder();
	public ModelBatch modelBatch;
	public Array<Disposable> disposables = new Array<Disposable>();
	public PerspectiveCamera camera;
	private CameraInputController camController;
	public AssetManager assets;
	public boolean loading;
	
	public GameScreen(final BaboViolentGame g) {
		//TEST
		MapLoader m = new MapLoader();
		m.test();
		
		Bullet.init();
		game = g;        
        environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
		light = new DirectionalLight();
		light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);
		environment.add(light);
		modelBatch = new ModelBatch();
		world = new BulletWorld();
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());;
		camera.position.set(10f, 100f, 10f);
		camera.lookAt(0, 0, 0);
		camera.far = 500;
		camera.update();
		
		camController = new CameraInputController(camera);
		camController.autoUpdate = true;
		Gdx.input.setInputProcessor(camController);
		
		assets = new AssetManager();
        assets.load("data/models/test_chaingun.g3dj", Model.class);
        assets.load("data/models/babo_explode.g3dj", Model.class);
        loading = true;
		
		final Model largeGroundModel = modelBuilder.createBox(
				10f,
				2f,
				10f,
				new Material(ColorAttribute.createDiffuse(Color.BLUE)) , Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		disposables.add(largeGroundModel);
		world.addConstructor("largeground", new BulletConstructor(largeGroundModel, 0f));
		world.add("largeground", 0, -1f, 0f);
    }
	
	private void doneLoading() {
		//if( !assets.isLoaded("data/test_chaingun.g3dj") || !assets.isLoaded("data/test_chaingun.g3dj") ))
        Model chaingun = assets.get("data/models/test_chaingun.g3dj", Model.class);
        world.addConstructor("chaingun", new BulletConstructor(chaingun, 0f));
        world.add("chaingun", 0, 3f, 0);
        
        Model babo = assets.get("data/models/babo_explode.g3dj", Model.class);
        world.addConstructor("babo", new BulletConstructor(babo, 0f));
        world.add("babo", 0, 3f, 0);
        loading = false;
    }
	
	@Override
	public void render(float delta) {
		if (loading && assets.update())
            doneLoading();
		
		update();
		beginRender();
		renderWorld();
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);		
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
		world.dispose();
		world = null;

		for (Disposable disposable : disposables)
			disposable.dispose();
		disposables.clear();

		modelBatch.dispose();
		modelBatch = null;

		light = null;
	}

	private void beginRender() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		//camera.update();
		camController.update();
	}
	
	private void renderWorld () {
		modelBatch.begin(camera);
		world.render(modelBatch, environment);
		modelBatch.end();
	}
	
	private void update () {
		world.update();
	}
}
