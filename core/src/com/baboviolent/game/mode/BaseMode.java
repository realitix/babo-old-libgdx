package com.baboviolent.game.mode;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.camera.ChaseCamera2;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.weapon.Shotgun;
import com.baboviolent.game.loader.ParticleLoader;
import com.baboviolent.game.map.Map;
import com.baboviolent.game.particle.PoolParticle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class BaseMode {
    protected final String mapName;
    protected Array<Babo> babos = new Array<Babo>();
    protected BulletWorld world;
    protected ChaseCamera2 camera;
    protected Babo player;
    protected ObjectMap<String, PoolParticle> particles = new ObjectMap<String, PoolParticle>();
	protected ParticleSystem particleSystem;
    
    public BaseMode(final String mapName) {
        this.mapName = mapName;
    }
    
    public BulletInstance getMapInstance() {
        return Map.loadInstance(mapName);
    }
    
    public void init() {
        // Initialisation du monde
		world = new BulletWorld();
		
		// Initialisation de la map
		world.add(Map.loadInstance(mapName));
		
		// Initialisation de la caméra
		camera = new ChaseCamera2();
		
		// Initialisation des particules
		particleSystem = ParticleLoader.init(camera);
		particles = ParticleLoader.getParticles();
		
		// Initialisation du joueur
        player = new Babo("skin22", particles.get("blood")).translate(new Vector3(800, 20, 600));
        world.add(player);
        babos.add(player);
        camera.init(player);
        
        // Initialisation de l'arme
        Shotgun shotgun = new Shotgun(world, particles.get("test"));
        world.attachWeaponToBabo(player, shotgun);
        
        // Creation d'un deuxieme joueur pour tester
		Babo b2 = new Babo("skin22", particles.get("blood")).translate(new Vector3(800, 20, 1000));
        world.add(b2);
        babos.add(b2);
        Shotgun shotgun2 = new Shotgun(world, particles.get("test"));
        world.attachWeaponToBabo(b2, shotgun2);
    }
    
    public Babo getPlayer() {
        return player;
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
    
    public Array<ModelInstance> getExplodingBabos() {
        Array<ModelInstance> b = new Array<ModelInstance>();
        for( int i = 0; i < babos.size; i++ ) {
		    if( babos.get(i).getState() == Babo.STATE_EXPLODE ) {
		        b.add(babos.get(i).getExplodingInstance());
		    }
		}
		
		return b;
    }
    
    public void mouseMoved(int screenX, int screenY) {
    	Vector3 position = Utils.getPositionFromMouse(new Vector3(), camera, screenX, screenY);
    	player.setTarget(position);
    }
    
    public void mouseClicked(int screenX, int screenY) {
    	player.shoot();
    }
    
    public void mouseReleased(int screenX, int screenY) {
    	player.stopShoot();
    }
    
    public void update() {
        camera.update();
		world.update();
		player.update(Utils.getPositionFromMouse(new Vector3(), camera));
		updateBabos();
		updateParticleSystem();
    }
    
    private void updateBabos() {
    	for(int i = 0; i < babos.size; i++) {
    		if( babos.get(i) != player ) {
    			babos.get(i).update();
    		}
    	}
    }
    
    private void updateParticleSystem() {
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