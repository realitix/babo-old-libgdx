package com.baboviolent.game.gameobject;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.gameobject.weapon.Weapon;
import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.particle.PoolParticle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.PolarAcceleration;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;

public class Babo extends GameObject {
    private static int idIncrement = 1;
    private int id = idIncrement++;
	private String skin;
	private Vector3 direction;
	private Vector3 target;
	private Weapon weapon;
	private int energy = 100;
	private boolean shooting = false;
	private final PoolParticle particule; // Particule émise lorsqu'on est touché par une balle
	
	public Babo(String skin, final PoolParticle particule) {
		this.particule = particule;
	    this.skin = skin;
	    name = "Babo";
	    type = GameObject.TYPE_BABO;
	    direction = new Vector3();
	    target = new Vector3();
	    
	    /**
	     * ///best simulation results when friction is non-zero
			btScalar m_friction;
			///the m_rollingFriction prevents rounded shapes, such as spheres, cylinders and capsules from rolling forever.
			///See Bullet/Demos/RollingFrictionDemo for usage
			btScalar m_rollingFriction;
			///best simulation results using zero restitution.
			btScalar m_restitution;
	     */
	    friction = 1f;
        rollingFriction = 0.6f;
        linearDamping = 0;
        angularDamping = 0.5f;
        restitution = 0;
        mass = 200;
        
        friction = 5f;
        rollingFriction = 7f;
        linearDamping = 0;
        angularDamping = 0.9f;
        restitution = 0.5f;
        mass = 2000;
        
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
    
    public Babo hit(int power) {
    	ParticleEffect effect = particule.obtain();
    	effect.init();
    	effect.reset();
        effect.start();
        effect.setTransform(this.instance.transform);
        ParticleSystem.get().add(effect);
        
    	energy -= power;
    	return this;
    }
    
    public int getEnergy() {
    	return energy;
    }
    
    public void update(Vector3 target) {
    	this.target.set(target);
    	this.weapon.lookAt(target);
    	if( shooting ) {
    		weapon.shoot(target);
    	}    	
    	
    	this.update();
    }
    
    // Algo ici: http://bulletphysics.org/Bullet/phpBB3/viewtopic.php?f=9&t=8487&view=previous
    public void update() {
    	float s1 = 10000000;
    	float s2 = 200000000;
    	
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