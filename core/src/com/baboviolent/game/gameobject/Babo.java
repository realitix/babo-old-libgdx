package com.baboviolent.game.gameobject;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Configuration;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.bullet.instance.BulletInstance;
import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.group.Blood1;
import com.baboviolent.game.gameobject.weapon.Weapon;
import com.baboviolent.game.hud.Hud;
import com.baboviolent.game.loader.TextureLoader;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.TimeUtils;

public class Babo extends GameObject {
	public static final int STATE_ALIVE = 1;
	public static final int STATE_EXPLODE = 2;
	public static final int STATE_DEAD = 3;
	public static final int STATE_APPEAR = 4; // Attend la reapparition
	public static final int ENERGY = 100;
	
    private long timeBeforeAppear;
    private long lastTimeDead;
    private boolean player;
	private String skin;
	private String username;
	private Vector3 direction;
	protected Vector3 target;
	private Weapon weapon;
	private int energy;
	private boolean shooting = false;
	private boolean moving = false;
	private final BaboEffectSystem effectSystem;
	private int state;
	private int score;
	private boolean manualDeath; // Si true, babo ne perd pas d'�nergie, bien pour multijoueur
	private Babo lastShooter; // Dernier babo ayant touch� ce babo
	private final BulletWorld world;
	private float maxSpeed;
	private float maxAcceleration;
	private Hud hud;
	
	public Babo(String username, String skin, BaboEffectSystem effectSystem, final BulletWorld world) {
		this.effectSystem = effectSystem;
	    this.skin = skin;
	    this.username = username;
	    this.world = world;
	    name = "Babo";
	    type = GameObject.TYPE_BABO;
	    timeBeforeAppear = 5000;
	    direction = new Vector3();
	    target = new Vector3();
	    state = STATE_ALIVE;
	    energy = ENERGY;
	    manualDeath = false;
	    
        friction = 100f;
        rollingFriction = 0.1f;
        linearDamping = 0.8f;
        angularDamping = 0.8f;
        restitution = 0.5f;
        mass = 20;
        maxSpeed = 350;
    	maxAcceleration = 400;
        
        target = new Vector3();
        
        initInstance();
	}
	
	@Override
	protected void initInstance() {
        float d = BaboViolentGame.BABO_DIAMETER;
        Material material = TextureLoader.getMaterial(skin, TextureLoader.TYPE_SKIN);
        int nbDivisions;
        switch( Configuration.Video.baboLevelOfDetail ) {
        	case Configuration.MIN:
        		nbDivisions = 8;
        		break;
        	case Configuration.MED:
        		nbDivisions = 10;
        		break;
        	case Configuration.MAX:
        		nbDivisions = 16;
        		break;
        	default:
        		nbDivisions = 10;
        }
        
        Model model =  new ModelBuilder().createSphere(
        	d, d, d, nbDivisions, nbDivisions,
        	material,
        	Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        
        shape = new btSphereShape(d/2);
        btRigidBody body = super.initBody(shape);
        
        // Empeche la desactvation des collisions
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        
        // Detection de la collision entre les balles et les babos
        body.setUserValue(id);
        body.setContactCallbackFlag(BulletContactListener.BABO_FLAG);
        
        // Creation de l'instance
        instance = new BulletInstance(model, body);
    }
    
    public Babo hit(float damage) {
    	effectSystem.get(Blood1.NAME).start(instance.transform, damage);
        
        if( !manualDeath ) {
        	energy -= damage;
        }
    	return this;
    }
    
    public void explode() {
    	state = STATE_EXPLODE;
    	lastTimeDead = TimeUtils.millis();
    	
    	// On cache l'instance pour ensuite faire apparaitre l'instance d'explosion
    	instance.nodes.get(0).parts.get(0).enabled = false;
    	// Desactive le body physic
        body.setActivationState(Collision.DISABLE_SIMULATION);
        
        // On genere beacoup de particules
        /*ParticleEffect effect = particules.get("blood2").obtain();
    	effect.init();
    	effect.reset();
        effect.start();
        effect.setTransform(this.instance.transform);
        ParticleSystem.get().add(effect);*/
        
        // On d�tache l'arme
        world.detachWeaponToBabo(this, weapon);
        
        // On ejecte l'arme
        weapon.eject();
    }
    
    // On reactive tout
    public void appear(Vector3 position) {
    	energy = ENERGY;
    	instance.nodes.get(0).parts.get(0).enabled = true;
    	this.teleport(position);
    	body.setActivationState(Collision.DISABLE_DEACTIVATION);
    	state = STATE_ALIVE;
    	
    	// On rattache l'arme
    	if( weapon != null ) {
    		world.attachWeaponToBabo(this, weapon);
    	}
    }
    
    public void update() {
    	updateState();
    	if( state == STATE_ALIVE ) {
    		if( weapon != null ) {
    			updateWeapon();
    		}
    		updateMovement();
    	}
    }
    
    private void updateState() {
    	if( state == STATE_EXPLODE ) {
    		state = STATE_DEAD;
    	}
    	
    	if( energy <= 0 && state == STATE_ALIVE ) {
    		explode();
    	}
    	
    	if( state == STATE_DEAD && TimeUtils.millis() - lastTimeDead > timeBeforeAppear ) {
    		state = STATE_APPEAR;
    	}
    }
    
    private void updateWeapon() {
    	if( target != null ) {
	    	weapon.lookAt(target);
	    	if( shooting ) {
	    		weapon.shoot(target);
	    	}
    	}
    }
    
    // Algo ici: http://bulletphysics.org/Bullet/phpBB3/viewtopic.php?f=9&t=8487&view=previous
    private void updateMovement() {    	
    	if( instance.body.getAngularVelocity().len2() < 1 && direction.isZero() ) {
    		moving = false;
    		return;
    	}
    	moving = true;
    	
    	// velocity_factor est ma direction
    	btRigidBody b = instance.body;
    	Vector3 maxVelocity = new Vector3(maxSpeed, maxSpeed, maxSpeed);
		Vector3 velocity = direction.cpy().scl(maxVelocity);
		Vector3 currentVelocity = b.getAngularVelocity(); // TODO: check ang vel component and coord. systs
		currentVelocity.set(currentVelocity.z, currentVelocity.y, currentVelocity.x);
		Vector3 deltaVelocity = velocity.sub(currentVelocity);
		
		float MIN_VELOCITY = 10;
		if( Math.abs(deltaVelocity.x) < MIN_VELOCITY )
			deltaVelocity.x = 0;
		if( Math.abs(deltaVelocity.z) < MIN_VELOCITY )
			deltaVelocity.z = 0;
		
		// acceleration ne doit pas etre egal a zero sur aucun composant
		Vector3 acceleration = new Vector3(maxAcceleration,maxAcceleration,maxAcceleration);
		// Inverse de l'acceleration
		Vector3 ai = acceleration.cpy();
		ai.set(1/ai.x, 1/ai.y, 1/ai.y);
		Vector3 dt = deltaVelocity.cpy().scl(ai);
		Vector3 torque = acceleration.cpy(); /* TODO: times mass inertia */;
		
		// TODO: add deltaV check
		if (dt.x == 0) torque.x = 0;
		else torque.x *= (dt.x < 0 ? -1 : 1);
		
		if (dt.y == 0) torque.y = 0;
		else torque.y *= (dt.y < 0 ? -1 : 1);
		
		if (dt.z == 0) torque.z = 0;
		else torque.z *= (dt.z < 0 ? -1 : 1);
		
		/*Matrix4 trans = new Matrix4();
		b.getMotionState().getWorldTransform(trans);
		trans.setTranslation(0, 0, 0);*/
		
		// Le vecteur torque indique sur quel axe on tourne. Si on veut aller vers x, il faut tourner sur l'axe z
		//b.applyTorque(new Vector3(torque.z, torque.y, torque.x));
		// Test avec impulsion centrale
		torque.y = 0;
		b.applyCentralForce(new Vector3(-torque.x, torque.y, torque.z));
    }
    
    public Babo teleport(Vector3 v) {
    	instance.body.setWorldTransform(instance.body.getWorldTransform().setToTranslation(v));
    	return this;
    }
    
    public Babo shoot() {
    	shooting = true;
        return this;
    }
    
    public Babo stopShoot() {
    	shooting = false;
        return this;
    }
    
    public boolean getShoot() {
        return shooting;
    }
    
    public Babo setShoot(boolean shoot) {
        shooting = shoot;
        return this;
    }
    
    public Babo addScore(int add) {
    	score += add;
        return this;
    }
    
    public Babo setDirection(Vector3 f) {
        direction.set(f.x, f.y, f.z);
        return this;
    }
    
    public Vector3 getDirection() {
        return direction.cpy();
    }
    
    public BulletWorld getWorld() {
        return world;
    }
    
    public BaboEffectSystem getEffectSystem() {
        return effectSystem;
    }
    
    public Babo setManualDeath(boolean d) {
        manualDeath = d;
        return this;
    }
    
    public boolean getManualDeath() {
        return manualDeath;
    }
    
    public Babo setTarget(Vector3 t) {
    	target.set(t);
        return this;
    }
    
    public Vector3 getTarget() {
        return target.cpy();
    }
    
    public Vector3 getPosition() {
        return instance.body.getCenterOfMassPosition().cpy();
    }
    
    public String getUsername() {
        return username;
    }
    
    public Babo setLastShooter(Babo babo) {
        lastShooter = babo;
        return this;
    }
    
    public Babo getLastShooter() {
        return lastShooter;
    }
    
    public Babo setWeapon(Weapon weapon) {
        this.weapon = weapon;
        return this;
    }
    
    public Weapon getWeapon() {
        return weapon;
    }
    
    public int getState() {
    	return state;
    }
    
    public int getEnergy() {
    	return energy;
    }
    
    public boolean isMoving() {
    	return moving;
    }
    
    public boolean isPlayer() {
    	return player;
    }
    
    public Babo setPlayer(boolean player) {
    	this.player = player;
    	return this;
    }
    
    public Babo setHud(Hud hud) {
    	this.hud = hud;
    	return this;
    }
    
    public Hud getHud() {
    	return hud;
    }
    
    public Vector3 getLinearVelocity() {
		tmpV3.set(this.body.getLinearVelocity()).y = 0;
		return tmpV3.cpy();
	}
    
    public Babo setLinearVelocity(Vector3 velocity) {
    	this.body.setLinearVelocity(velocity.cpy());
		return this;
	}
}