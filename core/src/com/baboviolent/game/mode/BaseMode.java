package com.baboviolent.game.mode;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.camera.ChaseCamera2;
import com.baboviolent.game.controller.BaboController;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.weapon.Shotgun;
import com.baboviolent.game.loader.ParticleLoader;
import com.baboviolent.game.map.Map;
import com.baboviolent.game.particle.PoolParticle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class BaseMode {
    protected Array<Babo> babos = new Array<Babo>();
    protected BulletWorld world;
    protected ChaseCamera2 camera;
    protected Babo player;
    protected ObjectMap<String, PoolParticle> particles = new ObjectMap<String, PoolParticle>();
	protected ParticleSystem particleSystem;
	private BaboController controller;
	protected Map map;
	protected int nbIa = 1;
	protected Vector3 tmpV = new Vector3();
    
    public BaseMode(final String mapName) {
        map = Map.load(mapName);
    }
    
    public BulletInstance getMapInstance() {
        return Map.loadInstance(map);
    }
    
    public void init() {
        // Initialisation du monde
		world = new BulletWorld();
		
		// Initialisation de la map
		world.add(Map.loadInstance(map));
		
		// Initialisation de la caméra
		camera = new ChaseCamera2();
		
		// Initialisation des particules
		particleSystem = ParticleLoader.init(camera);
		particles = ParticleLoader.getParticles();
		
		// Initialisation du controller
		controller = new BaboController(this);
		
		// Initialisation du joueur
		initPlayer();
        
        // Initialisation de l'intelligence artificielle
        initIa();
    }
    
    protected void initPlayer() {
        Babo player = initBabo(Gdx.app.getPreferences("com.baboviolent.game").getString("username"));
        this.player = player;
        camera.init(this.player);
    }
    
    protected Babo initBabo(String username) {
        Babo babo = new Babo(username, "skin22", particles).teleport(generateBaboPosition());
        world.add(babo);
        babos.add(babo);
        Shotgun shotgun = new Shotgun(world, particles.get("test"));
        world.attachWeaponToBabo(babo, shotgun);
        return babo;
    }
    
    protected void initIa() {
        for( int i = 0; i < nbIa; i++ ) {
            // Creation d'un deuxieme joueur pour tester
    		Babo b2 = new Babo("toto", "skin22", particles).teleport(new Vector3(800, 20, 1000));
            world.add(b2);
            babos.add(b2);
            Shotgun shotgun2 = new Shotgun(world, particles.get("test"));
            world.attachWeaponToBabo(b2, shotgun2);
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
    
    public ParticleSystem getParticleSystem() {
        return particleSystem;
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
    
    public void update() {
    	player.setTarget(controller.getTarget(camera));
    	
        camera.update();
		world.update();
		
		updateBabos();
		updateParticleSystem();
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
    	}
    }
    
    /**
     * Génère la position du nouveau babo
     * Algorythme: le plus loin
     * Pour chaque cellule de type sol
     * On additionne la distance de tous les babos et on prend la plus faible
     */ 
    protected Vector3 generateBaboPosition() {
    	Vector3 position = new Vector3();
    	Vector3 bp = new Vector3();
    	float min = 999999999;
    	for( int i = 0; i < map.getCells().size; i++ ) {
    	    if( map.getCells().get(i).getType().equals(Map.TYPE_GROUND) ) {
    	        Vector3 cp = map.getCells().get(i).getPosition();
    	        float sumTmp = 0;
        	    for( int j = 0; j < babos.size; j++ ) {
        	        if( babos.get(j).getState() == Babo.STATE_ALIVE ) {
        	            babos.get(j).getInstance().transform.getTranslation(bp);
        	            sumTmp += Vector3.dst(cp.x, cp.y, cp.z, bp.x, bp.y, bp.z);
        	        }
        	    }
        	    
        	    if( sumTmp < min ) {
        	        position.set(cp);
        	    }
    	    }
    	}
    	
    	return position;
    }
    
    protected void updateParticleSystem() {
		particleSystem.update();
		for (ObjectMap.Entry<String, PoolParticle> e : particles.entries()) {
			e.value.update();
        }
        
        particleSystem.begin();
		particleSystem.draw();
		particleSystem.end();
	}
    
    public void dispose() {
		world.dispose();
		world = null;
    }
}