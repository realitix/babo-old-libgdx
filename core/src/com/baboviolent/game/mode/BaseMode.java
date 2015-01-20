package com.baboviolent.game.mode;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.ai.AiBabo;
import com.baboviolent.game.ai.bullet.BulletRaycastCollisionDetector;
import com.baboviolent.game.ai.manager.AiManager;
import com.baboviolent.game.ai.pfa.tiled.flat.BaboPathGenerator;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.camera.ChaseCamera2;
import com.baboviolent.game.controller.BaboController;
import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.PoolParticle;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.weapon.Shotgun;
import com.baboviolent.game.loader.ParticleLoader;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.ParallelSideRayConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.SingleRayConfiguration;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class BaseMode {
    protected Array<Babo> babos = new Array<Babo>();
    protected BulletWorld world;
    protected ChaseCamera2 camera;
    protected Babo player;
    protected BaboEffectSystem effectSystem;
	protected BaboController controller;
	protected Map map;
	protected int nbIa;
	protected Vector3 tmpV = new Vector3();
	protected Decal cursor;
	protected Environment environment;
    
    public BaseMode(final String mapName) {
        map = Map.load(mapName);
    }
    
    public BulletInstance getMapInstance() {
        return Map.loadInstance(map);
    }
    
    public void init() {
    	// Initailisation de l'environement
    	environment = new Environment();
    	environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f));
		
    	// Initialisation de la camera
    	camera = new ChaseCamera2();
    			
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
        
        // Initialisation du curseur
        cursor = Decal.newDecal(new TextureRegion(
        		new Texture(Gdx.files.internal(BaboViolentGame.PATH_TEXTURE_OTHERS+"Cross01.png"))),
        		true);
    }
    
    protected void initPlayer() {
        Babo player = initBabo(Gdx.app.getPreferences("com.baboviolent.game").getString("username"));
        this.player = player;
        player.appear(generateBaboPosition());
        camera.init(this.player);
    }
    
    protected Babo initBabo(String username) {
        Babo babo = new Babo(username, "skin22", effectSystem.getParticleSystem(), world);
        world.add(babo);
        babos.add(babo);
        Shotgun shotgun = new Shotgun(babo, world, effectSystem);
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
    		AiBabo ai = new AiBabo("ai1", "skin22", effectSystem.getParticleSystem(), world, babos, pathGenerator);
    		world.add(ai);
    		babos.add(ai);
    		Shotgun shotgun = new Shotgun(ai, world, effectSystem);
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
    
    public void render(ModelBatch modelBatch, DecalBatch decalBatch) {
    	modelBatch.begin(camera);
    	world.render(modelBatch, environment);
    	effectSystem.render(modelBatch, decalBatch, environment);
    	modelBatch.end();
    	
    	// Le curseur
    	decalBatch.add(cursor);
    	
    	decalBatch.flush();
    }
    
    public void update() {
    	Vector3 target = controller.getTarget();
    	player.setTarget(target);
    	camera.setTarget(target);
    	
        camera.update();
		world.update();
		
		updateBabos();
		updateCursor(target);
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
    
    protected void updateCursor(Vector3 target) {
    	Vector3 t = target.cpy();
    	t.y = camera.position.y - 300;
    	cursor.setPosition(t);
    	cursor.lookAt(t.add(0, 20, 0 ), Vector3.Y);
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
    
    public void dispose() {
		world.dispose();
		world = null;
    }
}