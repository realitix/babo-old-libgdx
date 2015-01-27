package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.loader.BaboModelLoader;
import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.map.Cell;
import com.baboviolent.game.map.Map;
import com.baboviolent.game.map.MapObject;
import com.baboviolent.game.map.editor.UI;
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
	public static final Array<String> modelsToLoad = new Array<String>(new String[]{"flagpod"});
	
	private final BaboViolentGame game;
	private Environment environment;
	private DirectionalLight light;
	private Array<Disposable> disposables = new Array<Disposable>();
	private PerspectiveCamera camera;
	private CameraInputController cameraController;
	private ObjectMap<String, Model> models;
	private Array<ModelInstance> instances;
	private String currentType;
	private String currentObjectType; // Type d'objet à créer sélectionné
	private String currentCellTexture; // Type de sol à créer sélectionné
	private Model currentModel; // Modèle actuel de l'objet à créer
	private ModelInstance currentModelInstance; // Permet de suivre le curseur de la souris
	private ModelBatch modelBatch;
	private Map map;
	private UI ui;
	private boolean rotationInput = true;
	
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
		camera.position.set(0f, 3*BaboViolentGame.SIZE_MAP_CELL, 0f);
		camera.up.set(0, 0, 1);
		camera.lookAt(0, 0, 0);
		camera.far = 10000;
		camera.update();

        // Chargement des textures
        models = TextureLoader.getModels(TextureLoader.TYPE_GROUND);
        models.putAll(TextureLoader.getModels(TextureLoader.TYPE_WALL));
        models.putAll(BaboModelLoader.getModels(modelsToLoad));
        
        // Controle de la camera
        cameraController = new CameraInputController(camera);
        //cameraController.autoUpdate = true;
        //cameraController.rotateButton = Buttons.LEFT;
        //cameraController.forwardButton = -10; // On desactive le forward
        //cameraController.scrollFactor = -0.1f;
        //cameraController.translateUnits = 3000f;
        
		// Ajout de l'UI
		ui = new UI( this, !BaboViolentGame.isMobile() );
		
		// Ajout du controle
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor( ui.getStage() );
		im.addProcessor( cameraController );
        Gdx.input.setInputProcessor(im);
		
        // Creation d'une map
        map = new Map()
        	.setVersion(1)
        	.setAuthor("Test")
        	.setName("Name");
        
        // Creation des fleches indiquant les directions
        initGrid();
    }
    
	private void initGrid() {
		ModelBuilder mb = new ModelBuilder();
        float s = BaboViolentGame.SIZE_MAP_CELL;
        int gridX = 100;
        int gridZ = 100;
        // X = ROUGE, Y = VERT, Z = BLEU
        instances.add(new ModelInstance(mb.createXYZCoordinates( s*5, new Material(), Usage.Position | Usage.ColorUnpacked )));
        instances.add(new ModelInstance(mb.createLineGrid(gridX, gridZ, s, s, new Material(ColorAttribute.createDiffuse(Color.GRAY)), Usage.Position)));
	}
	
	/**
	 * Cette fonction permet de switcher entre rotation et deplacement
	 */
	public void switchInput() {
		if( rotationInput ) {
			cameraController.rotateButton = Buttons.RIGHT;
			cameraController.translateButton = Buttons.LEFT;
			rotationInput = false;
		}
		else {
			cameraController.rotateButton = Buttons.LEFT;
			cameraController.translateButton = Buttons.RIGHT;
			rotationInput = true;
		}
	}
	
    /**
     * Selectionne le sol
     */ 
    public void selectGround(String type) {
    	if( type == currentCellTexture && currentType == Map.TYPE_GROUND ) {
    		return;
    	}
    	
    	currentCellTexture = type;
    	currentModel = models.get(type);
    	if( currentModel != null ) {
    		currentType = Map.TYPE_GROUND;
        	currentModelInstance = new ModelInstance(currentModel);
    	}
    }
    
    /**
     * Selectionne le mur
     */ 
    public void selectWall(String type) {
    	if( type == currentCellTexture && currentType == Map.TYPE_WALL ) {
    		return;
    	}
    	
    	currentCellTexture = type;
    	currentModel = models.get(type);
    	
    	if( currentModel != null ) {
    		currentType = Map.TYPE_WALL;
        	currentModelInstance = new ModelInstance(currentModel);
    	}
    }
    
    /**
     * Selectionne un objet
     */ 
    public void selectObject(String type) {
    	if( type == currentObjectType && currentType == Map.TYPE_OBJECT ) {
    		return;
    	}
    	
    	if( currentModel == null ) {
    		return;
    	}
    	
    	currentObjectType = type;
    	currentModel = models.get(type);
    	currentType = Map.TYPE_OBJECT;
    	currentModelInstance = new ModelInstance(currentModel);
    }
    
    /**
     * Selectionne la gomme pour effacer les cellules
     */ 
    public void selectEraser() {
    	currentType = Map.TYPE_ERASER;
    	currentModelInstance = null;
    	currentModel = null;
    }
    
    /**
     * Deplacement de la souris
     */ 
    public void mouseMove(int screenX, int screenY) {
        moveCurrentModelInstance(screenX, screenY);
        ui.mouseMoved(screenX, screenY);
    }
    
    /**
     * Clic de la souris
     */ 
    public void mouseClick(int screenX, int screenY) {
    	if(currentType == Map.TYPE_ERASER)
    		deleteObject(screenX, screenY);
    	if( currentModel == null )
    		return;
    	if(currentType == Map.TYPE_GROUND)
    		createCell(screenX, screenY);
    	if(currentType == Map.TYPE_WALL)
    		createCell(screenX, screenY);
    	if(currentType == Map.TYPE_OBJECT)
    		createObject(screenX, screenY);
    }
    
    /**
     * Deplace l'instance du sol afin de suivre la souris
     */ 
    public void moveCurrentModelInstance(int screenX, int screenY) {
    	if(currentModelInstance != null) {
    		Vector3 position = getPositionFromMouse(screenX, screenY);
    		if(currentType == Map.TYPE_GROUND || currentType == Map.TYPE_WALL)
    			position = positionToGrid(position);
    		
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
    	i.userData = Map.TYPE_OBJECT;
    	instances.add(i);
    	
    	map.addObject(new MapObject().setPosition(position).setType(currentObjectType));
    }
    
    /**
     * Cree une nouvelle instance du model en parametre et l'ajoute a la map
     */ 
    public void createCell(int screenX, int screenY) {
    	Vector3 mousePosition = getPositionFromMouse(screenX, screenY);
    	ModelInstance i = new ModelInstance(currentModel);
    	i.transform.setTranslation(positionToGrid(mousePosition));
    	instances.add(i);
    	
    	String type = "";
    	if(currentType == Map.TYPE_GROUND) 
    		type = Map.TYPE_GROUND;
    	if(currentType == Map.TYPE_WALL)
    		type = Map.TYPE_WALL;
    	
    	i.userData = type;
    	map.addCell(new Cell().setPosition(positionToCell(mousePosition)).setTextureName(currentCellTexture).setType(type));
    }
    
    /**
     * Supprime l'objet ou la cellule a l'emplacement
     */ 
    public void deleteObject(int screenX, int screenY) {
    	Vector3 position = new Vector3();
    	Ray ray = camera.getPickRay(screenX, screenY);
    	 
        // On supprime l'objet
        for (int i = 0; i < instances.size; i++) {
        	if( instances.get(i).userData != Map.TYPE_OBJECT )
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
    
    /**
     * Permet de bien afficher 
     */
    private Vector3 positionToGrid(Vector3 position) {
    	float s = BaboViolentGame.SIZE_MAP_CELL;
    	Vector3 result = position.cpy();
    	result.x -= s/2;
    	result.z -= s/2;
    	result.x = s * Math.round(result.x/s) + s/2;
    	result.z = s * Math.round(result.z/s) + s/2;
    	result.y = 0;
		
    	return result;
    }
    
    /**
     * Renvoie la position de la cellule 
     */
    private Vector3 positionToCell(Vector3 position) {
    	float s = BaboViolentGame.SIZE_MAP_CELL;
    	Vector3 result = position.cpy();
    	result.x = (float) Math.ceil(result.x/s);
    	result.y = (float) Math.ceil(result.y/s);
    	result.z = (float) Math.ceil(result.z/s);
    	return result;
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
     * Nettoie la map en cours
     */ 
    public void clearMap() {
    	map = new Map();
    	instances = new Array<ModelInstance>();
		currentModelInstance = null;
		initGrid();
    }
    
	/**
     * Sauvegarde la map actuelle
     */ 
    public void saveMap(String mapName) {
    	Map.save(map, mapName);
    }
    
    /**
     * Charge la map en parametre
     */ 
    public void loadMap(String mapName) {
    	clearMap();
    	map = Map.load(mapName);
    	
    	// Chargement des cellules
    	for(int i = 0; i < map.getCells().size; i++ ) {
    		Cell c = map.getCells().get(i);
    		ModelInstance ci = new ModelInstance(models.get(c.getTextureName()));
    		ci.transform.setTranslation(c.getPosition());
    		ci.userData = c.getType();
    		instances.add(ci);
    	}
    	
    	// Chargement des objets
    	for(int i = 0; i < map.getObjects().size; i++ ) {
    		MapObject mo = map.getObjects().get(i);
    		ModelInstance moi = new ModelInstance(models.get(mo.getType()));
    		moi.transform.setTranslation(mo.getPosition());
    		moi.userData = Map.TYPE_OBJECT;
    		instances.add(moi);
    	}
    }
    
    
	@Override
	public void render(float delta) {
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(255, 255, 255, 1);
	    //Gdx.gl.glClearColor(0, 0, 0, 1);
		
		update();
		
	    modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		if( currentModelInstance != null) modelBatch.render(currentModelInstance, environment);
		modelBatch.end();
		
		ui.draw();
	}
	
	private void update() {
		ui.update( Gdx.graphics.getDeltaTime() );
		cameraController.update();
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
