package com.baboviolent.game.gameobject;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.gameobject.weapon.Weapon;
import com.baboviolent.game.loader.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;

public class Babo extends GameObject {
	private String skin;
	private Vector3 direction;
	private Vector3 target;
	private Weapon weapon;
	
	public Babo(String skin) {
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
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        instance = new BulletInstance(model, body);
    }
    
    public Babo shoot(Vector3 target) {
    	weapon.shoot(target);
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
        updateWeapon();
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
    
    public void updateWeapon() {
    	this.weapon.lookAt(target);
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
		
		float SIMD_EPSILON = 1.1920928955078125E-7f;
		if( Math.abs(deltaVelocity.x) < SIMD_EPSILON )
			deltaVelocity.x = 0;
		if( Math.abs(deltaVelocity.z) < SIMD_EPSILON )
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
}