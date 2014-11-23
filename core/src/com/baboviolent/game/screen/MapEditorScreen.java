package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.loader.BaboModelLoader;
import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.map.Cell;
import com.baboviolent.game.map.Map;
import com.baboviolent.game.map.MapObject;
import com.baboviolent.game.map.editor.EditorInputAdapter;
import com.baboviolent.game.map.editor.Menu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class MapEditorScreen implements Screen {
	public static final String TYPE_ERASER = "type_eraser";
	public static final String TYPE_GROUND = "type_ground";
	public static final String TYPE_WALL = "type_wall";
	public static final String TYPE_OBJECT = "type_object";
	
	private final BaboViolentGame game;
	private Environment environment;
	private DirectionalLight light;
	private Array<Disposable> disposables = new Array<Disposable>();
	private PerspectiveCamera camera;
	private CameraInputController cameraController;
	private ObjectMap<String, Model> models;
	private Array<ModelInstance> instances;
	private Menu menu;
	private String currentType;
	private String currentObjectType; // Type d'objet à créer sélectionné
	private String currentCellTexture; // Type de sol à créer sélectionné
	private Model currentModel; // Modèle actuel de l'objet à créer
	private ModelInstance currentModelInstance; // Permet de suivre le curseur de la souris
	private ModelBatch modelBatch;
	private Map map;
	
	public MapEditorScreen(final BaboViolentGame g) {
		game = g;        
        environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
		light = new DirectionalLight();
		light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);
		environment.add(light);
		modelBatch = new ModelBatch();
		instances = new Array<ModelInstance>();
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());;
		camera.position.set(0f, 600f, 0f);
		camera.lookAt(0, 0, 0);
		camera.far = 10000;
		camera.update();

        // Chargement des textures
        models = TextureLoader.getGroundModels();
        models.putAll(BaboModelLoader.getModels());
        
        // Chargement du menu
        menu = new Menu(this);
        
        // Contrôle de la camera
        cameraController = new CameraInputController(camera);
        cameraController.autoUpdate = true;
        cameraController.rotateButton = Buttons.MIDDLE;
        cameraController.forwardButton = -10; // On désactive le forward
        cameraController.scrollFactor = -0.1f;
        cameraController.translateUnits = 400f;
        
        // Ajout du contrôle
        Gdx.input.setInputProcessor(new InputMultiplexer(
        	menu.getStage(),
        	cameraController,
        	new EditorInputAdapter(this)        	
        ));
        
        // Création d'une map
        map = new Map()
        	.setVersion(1)
        	.setAuthor("Test")
        	.setName("Name");
    }
    
    /**
     * Sélectionne le sol
     */ 
    public void selectGround(String type) {
    	if( type == currentCellTexture && currentType == MapEditorScreen.TYPE_GROUND ) {
    		return;
    	}
    	
    	currentCellTexture = type;
    	currentModel = models.get(type);
    	currentType = MapEditorScreen.TYPE_GROUND;
    	currentModelInstance = new ModelInstance(currentModel);
    }
    
    /**
     * Sélectionne un objet
     */ 
    public void selectObject(String type) {
    	if( type == currentObjectType && currentType == MapEditorScreen.TYPE_OBJECT ) {
    		return;
    	}
    	
    	currentObjectType = type;
    	currentModel = models.get(type);
    	currentType = MapEditorScreen.TYPE_OBJECT;
    	currentModelInstance = new ModelInstance(currentModel);
    }
    
    /**
     * Sélectionne la gomme pour effacer les cellules
     */ 
    public void selectEraser() {
    	currentType = MapEditorScreen.TYPE_ERASER;
    	currentModelInstance = null;
    	currentModel = null;
    }
    
    /**
     * Déplacement de la souris
     */ 
    public void mouseMove(int screenX, int screenY) {
        moveCurrentModelInstance(screenX, screenY);
    }
    
    /**
     * Clic de la souris
     */ 
    public void mouseClick(int screenX, int screenY) {
    	if(currentType == TYPE_ERASER)
    		deleteObject(screenX, screenY);
    	if( currentModel == null )
    		return;
    	if(currentType == TYPE_GROUND)
    		createCell(screenX, screenY);
    	if(currentType == TYPE_OBJECT)
    		createObject(screenX, screenY);
    }
    
    /**
     * Déplace l'instance du sol afin de suivre la souris'
     */ 
    public void moveCurrentModelInstance(int screenX, int screenY) {
    	if(currentModelInstance != null) {
    		Vector3 position = getPositionFromMouse(screenX, screenY);
    		if(currentType == TYPE_GROUND)
    			position = positionToCell(position);
    		
	        currentModelInstance.transform.setToTranslation(position);
    	}
    }
    
    /**
     * Supprime une cellule ou un object
     */ 
    public void createObject(int screenX, int screenY) {
    	Vector3 position = getPositionFromMouse(screenX, screenY);
    	ModelInstance i = new ModelInstance(currentModel);
    	i.transform.setTranslation(position);
    	i.userData = TYPE_OBJECT;
    	instances.add(i);
    	
    	map.addObject(new MapObject().setPosition(position).setType(currentObjectType));
    }
    
    /**
     * Créer une nouvelle instance du model en paramètre et l'ajoute à la map
     */ 
    public void createCell(int screenX, int screenY) {
    	Vector3 position = positionToCell(getPositionFromMouse(screenX, screenY));
    	ModelInstance i = new ModelInstance(currentModel);
    	i.transform.setTranslation(position);
    	i.userData = TYPE_GROUND;
    	instances.add(i);
    	
    	int type = 0;
    	if(currentType == TYPE_GROUND) 
    		type = Cell.TYPE_GROUND;
    	if(currentType == TYPE_WALL)
    		type = Cell.TYPE_WALL;
    	
    	map.addCell(new Cell().setPosition(position).setTextureName(currentCellTexture).setType(type));
    }
    
    /**
     * Supprime l'objet ou la cellule à l'emplacement
     */ 
    public void deleteObject(int screenX, int screenY) {
    	Vector3 position = new Vector3();
    	Ray ray = camera.getPickRay(screenX, screenY);
    	 
        // On supprime l'objet
        for (int i = 0; i < instances.size; i++) {
        	if( instances.get(i).userData != TYPE_OBJECT )
        		continue;
        	
            final ModelInstance instance = instances.get(i);
            instance.transform.getTranslation(position);     
     
            if (Intersector.intersectRaySphere(ray, position, 100, null)) {
                instances.removeIndex(i);
                map.removeObject(position);
                return;
            }
        }
        
        // On supprime la cellule
        for (int i = 0; i < instances.size; i++) {        	
            final ModelInstance instance = instances.get(i);
            instance.transform.getTranslation(position);
            Vector3 positionCenter = position.cpy();
            positionCenter.add(BaboViolentGame.SIZE_MAP_CELL/2, 0, BaboViolentGame.SIZE_MAP_CELL/2);
     
            if (Intersector.intersectRaySphere(ray, positionCenter, BaboViolentGame.SIZE_MAP_CELL/2, null)) {
            	map.removeCell(position);
                instances.removeIndex(i);
                return;
            }
        }
    }
    
    private Vector3 positionToCell(Vector3 position) {
    	float s = BaboViolentGame.SIZE_MAP_CELL;
        position.x = s * Math.round(position.x/s);
        position.z = s * Math.round(position.z/s);
        position.y = 0;
		position.x -= BaboViolentGame.SIZE_MAP_CELL/2;
		position.z -= BaboViolentGame.SIZE_MAP_CELL/2;
    	return position;
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
	
	/**
     * Sauvegarde la map actuelle'
     */ 
    public void saveMap(String mapName) {
    	Map.save(map, mapName);
    }
    
    
	@Override
	public void render(float delta) {
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(255, 255, 255, 1);
	    //Gdx.gl.glClearColor(0, 0, 0, 1);
		
		cameraController.update();
		
	    modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		if( currentModelInstance != null) modelBatch.render(currentModelInstance, environment);
		modelBatch.end();
		
		menu.render();
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
		for (Disposable disposable : disposables)
			disposable.dispose();
		disposables.clear();

		modelBatch.dispose();
		modelBatch = null;

		light = null;
	}
}
