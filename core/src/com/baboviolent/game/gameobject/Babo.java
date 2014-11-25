package com.baboviolent.game.gameobject;

import com.baboviolent.game.bullet.BulletInstance;

public class Babo extends GameObject {
	private String skin;
	private Vector3 force;
	
	public Babo(String skin) {
	    this.skin = skin;
	    force = new Vector3();
	    initPhysics();
        initInstance();
	}
	
	private initPhysics() {
	    friction = 0;
        rollingFriction = 0;
        linearDamping = 0;
        angularDamping = 0;
        restitution = 0;
        mass = 0;
	}
	
	@Override
	private void initInstance() {
        float d = BaboViolentGame.BABO_DIAMETER;
        Material material = TextureLoader.getMaterial("skin01", TextureLoader.TYPE_SKIN);
        Model model =  new ModelBuilder().createSphere(
        	d, d, d, 10, 10,
        	new Material(ColorAttribute.createDiffuse(Color.RED)), 
        	Usage.Position | Usage.Normal);
        
        shape = new btSphereShape(d/2);
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);
        
        ci = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        ci.setFriction(friction);
        ci.setRollingFriction(rollingFriction);
        ci.setLinearDamping(linearDamping);
        ci.setAngularDamping(angularDamping);
        ci.setRestitution(restitution);
        
        instance = new BulletInstance(model, ci);
    }
    
    public Babo setForce(Vector3 f) {
        force.set(f.x, f.y, f.z);
    }
    
    public void update() {
        if( !force.isZero() ) {
        	force.scl(Gdx.graphics.getDeltaTime());
            instance.body.applyCentralForce(force);
        }
    }
}