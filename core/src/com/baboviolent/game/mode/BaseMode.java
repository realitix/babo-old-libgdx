package com.baboviolent.game.mode;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.ai.AiBabo;
import com.baboviolent.game.ai.pfa.tiled.flat.BaboPathGenerator;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.camera.BaboCamera;
import com.baboviolent.game.controller.BaboController;
import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.weapon.Shotgun;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
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
	protected int nbIa;
	protected Vector3 tmpV = new Vector3();
	protected Environment environment;
    
    public BaseMode(final String mapName) {
        map = Map.load(mapName);
    }
    
    public BulletInstance getMapInstance() {
        return Map.loadInstance(map);
    }
    
    public void init() {
    	// Initialisation de l'environement
    	environment = new Environment();
    	environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1));
		
    	// Initialisation de la camera
    	camera = new BaboCamera(map);
    			
        // Initialisation du monde
		world = new BulletWorld();
		world.setCamera(camera);
		
		// Initialisation de la map
		world.add(Map.loadInstance(map));
		
		// Initialisation des particules
		effectSystem = new BaboEffectSystem(camera, environment);
		
		// Initialisation du controller
		controller = new BaboController(this, camera);
		
		// Initialisation du joueur
		initPlayer();
        
        // Initialisation de l'intelligence artificielle
        initIa();
    }
    
    protected void initPlayer() {
        Babo player = initBabo(Gdx.app.getPreferences("com.baboviolent.game").getString("username")).setPlayer(true);
        this.player = player;
        player.appear(generateBaboPosition());
        camera.init(this.player);
    }
    
    protected Babo initBabo(String username) {
        Babo babo = new Babo(username, "skin22", effectSystem, world);
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
    		AiBabo ai = new AiBabo("ai1", "skin22", effectSystem, world, babos, pathGenerator);
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
    public void render(ModelBatch modelBatch, ModelBatch shadowBatch, DecalBatch decalBatch) {
    	DirectionalShadowLight shadowLight = effectSystem.getLightSystem().getShadowLight();
    	shadowLight.begin(Vector3.Zero, camera.direction);
		shadowBatch.begin(shadowLight.getCamera());
		world.render(shadowBatch);
		shadowBatch.end();
		shadowLight.end();
		
    	modelBatch.begin(camera);
    	world.render(modelBatch, environment);
    	modelBatch.end();    	
    	
    	effectSystem.render(decalBatch);
    	decalBatch.flush();
    	
    	modelBatch.begin(camera);
    	effectSystem.render(modelBatch, environment);
    	modelBatch.end();
    }
    
    public void update() {
    	Vector3 target = controller.getTarget();
    	player.setTarget(target);
    	camera.setTarget(target);
    	
        camera.update();
		world.update();
		
		updateBabos();
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
    	        Vector3 cp = map.getCells().get(i).getAbsolutePosition();
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