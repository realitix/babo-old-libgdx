package com.baboviolent.game.gameobject;

import java.util.Random;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.gameobject.weapon.Weapon;
import com.baboviolent.game.loader.BaboModelLoader;
import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.particle.PoolParticle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.PolarAcceleration;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

public class Babo extends GameObject {
	public static final int STATE_ALIVE = 1;
	public static final int STATE_DEAD = 2;
	public static final int STATE_APPEAR = 3; // Attend la réapparition
	public static final int ENERGY = 100;
	
    private static int idIncrement = 1;
    private int id = idIncrement++;
    private long timeBeforeAppear;
    private long lastTimeDead;
	private String skin;
	private String username;
	private Vector3 direction;
	private Vector3 target;
	private Weapon weapon;
	private int energy;
	private boolean shooting = false;
	private boolean moving = false;
	private final ObjectMap<String, PoolParticle> particules; // Particule émise lorsqu'on est touché par une balle
	private Array<AnimationController> explodingControllers = new Array<AnimationController>();
	private int state;
	
	public Babo(String username, String skin, final ObjectMap<String, PoolParticle> particules) {
		this.particules = particules;
	    this.skin = skin;
	    this.username = username;
	    name = "Babo";
	    type = GameObject.TYPE_BABO;
	    timeBeforeAppear = 5000;
	    direction = new Vector3();
	    target = new Vector3();
	    state = STATE_ALIVE;
	    energy = ENERGY;
        friction = 100f;
        rollingFriction = 150f;
        linearDamping = 0.8f;
        angularDamping = 0.9f;
        restitution = 0.5f;
        mass = 2000;
        target = new Vector3();
        
        initInstance();
	}
	
	@Override
	protected void initInstance() {
        float d = BaboViolentGame.BABO_DIAMETER;
        Material material = TextureLoader.getMaterial(skin, TextureLoader.TYPE_SKIN);
        Model model =  new ModelBuilder().createSphere(
        	d, d, d, 10, 10,
        	material,
        	Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        
        shape = new btSphereShape(d/2);
        btRigidBody body = super.initBody(shape);
        
        // Empeche la desactvation des collisions
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        
        // Detection de la collision entre les balles et les babos
        body.setUserValue(id);
        body.setContactCallbackFlag(BulletContactListener.PLAYER_FLAG);
        
        // création de l'instance
        instance = new BulletInstance(model, body);
    }
    
    public Babo shoot() {
    	shooting = true;
        return this;
    }
    
    public Babo stopShoot() {
    	shooting = false;
        return this;
    }
    
    public Babo setDirection(Vector3 f) {
        direction.set(f.x, f.y, f.z);
        return this;
    }
    
    public Vector3 getDirection() {
        return direction.cpy();
    }
    
    public Babo setTarget(Vector3 f) {
        target.set(f.x, f.y, f.z);
        return this;
    }
    
    public Vector3 getTarget() {
        return target.cpy();
    }
    
    public String getUsername() {
        return username;
    }
    
    public Babo setWeapon(Weapon weapon) {
        this.weapon = weapon;
        return this;
    }
    
    public Weapon getWeapon() {
        return weapon;
    }
    
    public int getId() {
        return id;
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
    
    public Babo hit(int power) {
    	ParticleEffect effect = particules.get("blood").obtain();
    	effect.init();
    	effect.reset();
        effect.start();
        effect.setTransform(this.instance.transform);
        ParticleSystem.get().add(effect);
        
    	energy -= power;
    	return this;
    }
    
    public void explode() {
    	state = STATE_DEAD;
    	lastTimeDead = TimeUtils.millis();
    	
    	// On cache l'instance pour ensuite faire apparaitre l'instance d'explosion
    	instance.nodes.get(0).parts.get(0).enabled = false;
    	// Désactive le body physic
        body.setActivationState(Collision.DISABLE_SIMULATION);
        
        // On génère beacoup de particules
        ParticleEffect effect = particules.get("blood2").obtain();
    	effect.init();
    	effect.reset();
        effect.start();
        effect.setTransform(this.instance.transform);
        ParticleSystem.get().add(effect);
        
        // On ejecte l'arme
        Random rand = new Random();
        int max = 3000000;
        int min = 0;
        weapon.body.setAngularFactor(new Vector3(1,1,1));
        weapon.body.applyCentralImpulse(new Vector3(rand.nextInt((max - min) + 1) + min, rand.nextInt((max - min) + 1) + min, rand.nextInt((max - min) + 1) + min));
    }
    
    // On reactive tout
    public void appear(Vector3 position) {
    	energy = ENERGY;
    	instance.nodes.get(0).parts.get(0).enabled = true;
    	instance.transform.setToTranslation(position);
    	body.setActivationState(Collision.DISABLE_DEACTIVATION);
    	weapon.body.setAngularFactor(new Vector3(0,1,0));
    	state = STATE_ALIVE;
    }
    
    public void update() {
    	checkEnergy();
    	updateWeapon();
    	updateMovement();
    }
    
    public void checkEnergy() {
    	if( energy <= 0 && state == STATE_ALIVE ) {
    		explode();
    	}
    	
    	if( state == STATE_DEAD && TimeUtils.millis() - lastTimeDead > timeBeforeAppear ) {
    		state = STATE_APPEAR;
    	}
    }
    
    private void updateWeapon() {
    	weapon.lookAt(target);
    	if( shooting ) {
    		weapon.shoot(target);
    	}
    }
    
    // Algo ici: http://bulletphysics.org/Bullet/phpBB3/viewtopic.php?f=9&t=8487&view=previous
    private void updateMovement() {
    	float s1 = 10000000;
    	float s2 = 200000000;
    	
    	if( instance.body.getAngularVelocity().isZero() && direction.isZero() ) {
    		moving = false;
    		return;
    	}
    	moving = true;
    	
    	// velocity_factor est ma direction
    	btRigidBody b = instance.body;
    	Vector3 maxVelocity = new Vector3(s1, s1, s1);
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
		Vector3 acceleration = new Vector3(s2,s2,s2);
		// Inverse de l'accélération
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
		
		Matrix4 trans = new Matrix4();
		b.getMotionState().getWorldTransform(trans);
		trans.setTranslation(0, 0, 0);
		
		// Le vecteur torque indique sur quel axe on tourne. Si on veut aller vers x, il faut tourner sur l'axe z
		b.applyTorque(new Vector3(torque.z, torque.y, torque.x));
    }
    
    public Babo translate(Vector3 v) {
    	return this;
    }
}