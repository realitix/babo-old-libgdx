package com.baboviolent.game.mode;

import com.baboviolent.game.Utils;
import com.baboviolent.game.ai.BaboAi;
import com.baboviolent.game.bullet.BulletContactListener;
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
import com.badlogic.gdx.ai.steer.behaviors.Seek;
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
        player.appear(generateBaboPosition());
    }
    
    protected Babo initBabo(String username) {
        Babo babo = new Babo(username, "skin22", particles, world);
        world.add(babo);
        babos.add(babo);
        Shotgun shotgun = new Shotgun(babo, world, particles.get("test"));
        world.add(shotgun);
        babo.setWeapon(shotgun);
        BulletContactListener.addObject(babo);
        return babo;
    }
    
    protected void initIa() {
    	// @TODO seulement des tests
    	BaboAi ai1 = new BaboAi("ai1", "skin22", particles, world);
    	BaboAi ai2 = new BaboAi("ai2", "skin22", particles, world);
    	world.add(ai1);
    	world.add(ai2);
        babos.add(ai1);
        babos.add(ai2);
        Shotgun shotgun1 = new Shotgun(ai1, world, particles.get("test"));
        Shotgun shotgun2 = new Shotgun(ai2, world, particles.get("test"));
        world.add(shotgun1);
        world.add(shotgun2);
        ai1.setWeapon(shotgun1);
        ai2.setWeapon(shotgun2);
        BulletContactListener.addObject(ai1);
        BulletContactListener.addObject(ai2);
        
        Seek<Vector3> seekSB = new Seek<Vector3>(ai1, ai2);
		ai1.setSteeringBehavior(seekSB);
		
		ai1.appear(generateBaboPosition());
		ai2.appear(generateBaboPosition());
		
        for( int i = 0; i < nbIa; i++ ) {
            // Creation d'un deuxieme joueur pour tester
    		/*Babo b2 = new Babo("toto", "skin22", particles).teleport(new Vector3(800, 20, 1000));
            world.add(b2);
            babos.add(b2);
            Shotgun shotgun2 = new Shotgun(b2, world, particles.get("test"));
            world.attachWeaponToBabo(b2, shotgun2);*/
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