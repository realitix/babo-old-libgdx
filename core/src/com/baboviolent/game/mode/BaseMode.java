package com.baboviolent.game.mode;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Configuration;
import com.baboviolent.game.ai.AiBabo;
import com.baboviolent.game.ai.pfa.tiled.flat.BaboPathGenerator;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.bullet.instance.BulletInstance;
import com.baboviolent.game.camera.BaboCamera;
import com.baboviolent.game.controller.BaboController;
import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.weapon.Shotgun;
import com.baboviolent.game.gdx.batch.BaboModelBatch;
import com.baboviolent.game.gdx.batch.BaboSpriteBatch;
import com.baboviolent.game.gdx.decal.BaboCameraGroupStrategy;
import com.baboviolent.game.gdx.decal.BaboDecalBatch;
import com.baboviolent.game.gdx.shader.BaboShaderProvider;
import com.baboviolent.game.gdx.texture.BaboTextureBinder;
import com.baboviolent.game.hud.Hud;
import com.baboviolent.game.map.Map;
import com.baboviolent.game.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("deprecation")
public class BaseMode {
    protected Array<Babo> babos = new Array<Babo>();
    protected BulletWorld world;
    protected BaboCamera camera;
    protected Babo player;
    protected BaboEffectSystem effectSystem;
	protected BaboController controller;
	protected Map map;
	protected Hud hud;
	protected int nbIa;
	protected Vector3 tmpV = new Vector3();
	protected Environment environment;
	
	protected BaboModelBatch modelBatch;
	protected BaboDecalBatch decalBatch;
	protected BaboModelBatch shadowBatch;
	protected BaboSpriteBatch spriteBatch;
	protected RenderContext renderContext;
    
    public BaseMode(final String mapName) {
        map = Map.load(mapName);
    }
    
    public BulletInstance getMapInstance() {
        return Map.loadInstance(map);
    }
    
    public void init() {
    	// Initialisation de la camera
    	camera = new BaboCamera(map);
    	
    	// Initialisation des batches
    	initBatches();
    	
    	// Initialisation de l'environement
    	environment = new Environment();
    	environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1));
    			
        // Initialisation du monde
		world = new BulletWorld();
		world.setCamera(camera);
		
		// Initialisation de la map
		world.add(Map.loadInstance(map));
		
		// Initialisation des particules
		effectSystem = new BaboEffectSystem(camera, environment);
		
		// Initialisation du controller
		controller = new BaboController(this, camera);
		
		// Initialisation du Hud
        hud = new Hud(spriteBatch);
		
		// Initialisation du joueur
		initPlayer();
        
        // Initialisation de l'intelligence artificielle
        initIa();
    }
    
    private void initBatches() {
    	renderContext = new RenderContext(new BaboTextureBinder());
		modelBatch = new BaboModelBatch(renderContext, new BaboShaderProvider());
		shadowBatch = new BaboModelBatch(renderContext, new DepthShaderProvider());
		decalBatch = new BaboDecalBatch(new BaboCameraGroupStrategy(camera), renderContext);
		spriteBatch = new BaboSpriteBatch(renderContext);
    }
    
    protected void initPlayer() {
    	String name = Gdx.app.getPreferences("com.baboviolent.game").getString("username");
        Babo player = initBabo(name, true);
        this.player = player;
        player.appear(generateBaboPosition());
        camera.init(this.player);
    }
    
    protected Babo initBabo(String username, boolean player) {
        Babo babo = new Babo(username, "DyedStonework", effectSystem, world);
        if(player) {
        	babo.setPlayer(true).setHud(hud);
        }
        
        world.add(babo);
        babos.add(babo);
        Shotgun shotgun = new Shotgun(babo);
        world.add(shotgun);
        babo.setWeapon(shotgun);
        BulletContactListener.addObject(babo);
        return babo;
    }
    
    protected void initIa() {
    	BaboPathGenerator pathGenerator = null;
    	if( nbIa > 0 ) {
    		pathGenerator = new BaboPathGenerator(map);
    	}
    	
    	for( int i = 0; i < nbIa; i++) {
    		AiBabo ai = new AiBabo("ai1", "DyedStonework", effectSystem, world, babos, pathGenerator);
    		world.add(ai);
    		babos.add(ai);
    		Shotgun shotgun = new Shotgun(ai);
    		world.add(shotgun);
    		ai.setWeapon(shotgun);
    		BulletContactListener.addObject(ai);
    		ai.appear(generateBaboPosition());
    	}
    }
    
    public Babo getPlayer() {
        return player;
    }
    
    public BaboController getController() {
        return controller;
    }
    
    public Array<Babo> getBabos() {
        return babos;
    }
    
    public BulletWorld getWorld() {
        return world;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public BaboSpriteBatch getSpriteBatch() {
    	return spriteBatch;
    }
    
    public void onStartShoot() {
    	player.shoot();
    }
    
    public void onStopShoot() {
    	player.stopShoot();
    }
    
    public void onSetPlayerDirection(Vector3 direction) {
        player.setDirection(direction);
    }
    
    /**
     * Il faut afficher dans le bon sens car on a beacoup d'effet de transarence
     * Donc il faut afficher du plus bas au plus haut
     * On affiche d'abord le monde
     * ensuite les decals (sang, marques au sol...)
     * Puis enfin les particules
     */
    public void render() {
    	renderContext.begin();
    	if( Configuration.Video.enableShadow ) {
	    	DirectionalShadowLight shadowLight = effectSystem.getLightSystem().getShadowLight();
	    	shadowLight.begin(camera.position, camera.direction);
			shadowBatch.begin(shadowLight.getCamera());
			world.renderShadow(shadowBatch);
			shadowBatch.end();
			shadowLight.end();
    	}
    	
    	modelBatch.begin(camera);
    	world.render(modelBatch, environment);
    	modelBatch.end();   
    	
    	effectSystem.renderDecals(decalBatch);
    	renderContext.end();
    	
    	renderContext.begin();
    	modelBatch.begin(camera);
    	effectSystem.render(modelBatch, environment);
    	modelBatch.end();
    	renderContext.end();
    	
    	renderContext.begin();
    	effectSystem.renderCursor(decalBatch);
    	renderContext.end();
    	
    	hud.render();
    	controller.render();
    }
    
    public void update() {
    	Vector3 target = controller.getTarget();
    	player.setTarget(target);
    	camera.setTarget(target);
    	
        camera.update();
		world.update();
		effectSystem.update();
		hud.update();
		updateBabos();
		controller.update();
    }
    
    protected Vector3 getTarget() {
    	return Utils.getPositionFromMouse(tmpV, camera);
    }
    
    protected void updateBabos() {
    	for(int i = 0; i < babos.size; i++) {
    		babos.get(i).update();
    		
    		if( babos.get(i).getState() == Babo.STATE_APPEAR ) {
    			babos.get(i).appear(generateBaboPosition());
    		}
    		
    		if( babos.get(i).getState() == Babo.STATE_EXPLODE ) {
    			babos.get(i).getLastShooter().addScore(1);
    		}
    	}
    }
    
    /**
     * Genere la position du nouveau babo
     * Algorithme: le plus loin
     * Pour chaque cellule de type sol
     * On additionne la distance de tous les babos et on prend la plus eleve
     */ 
    protected Vector3 generateBaboPosition() {
    	Vector3 position = new Vector3();
    	Vector3 bp = new Vector3();
    	float max = 0;
    	for( int i = 0; i < map.getCells().size; i++ ) {
    	    if( map.getCells().get(i).getType().equals(Map.TYPE_GROUND) ) {
    	        Vector3 cp = map.getCells().get(i).getPosition();
    	        float sumTmp = 0;
        	    for( int j = 0; j < babos.size; j++ ) {
        	        if( babos.get(j).getState() == Babo.STATE_ALIVE ) {
        	            bp = babos.get(j).getPosition();
        	            sumTmp += Vector3.dst(cp.x, cp.y, cp.z, bp.x, bp.y, bp.z);
        	        }
        	    }
        	    
        	    if( sumTmp > max ) {
        	    	max = sumTmp;
        	        position.set(cp);
        	    }
    	    }
    	}
    	
    	position.y = BaboViolentGame.BABO_DIAMETER + 0.001f;
    	
    	return position;
    }
    
    public void dispose() {
		world.dispose();
		world = null;
    }
}