package com.baboviolent.game.gameobject;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.loader.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Babo extends GameObject {
	private String skin;
	private Vector3 force;
	
	public Babo(String skin) {
	    this.skin = skin;
	    force = new Vector3();
	    initPhysics();
        initInstance();
	}
	
	private void initPhysics() {
	    friction = 0;
        rollingFriction = 0;
        linearDamping = 0;
        angularDamping = 0;
        restitution = 0;
        mass = 1;
	}
	
	@Override
	protected void initInstance() {
        float d = BaboViolentGame.BABO_DIAMETER;
        Material material = TextureLoader.getMaterial(skin, TextureLoader.TYPE_SKIN);
        Model model =  new ModelBuilder().createSphere(
        	d, d, d, 10, 10,
        	//new Material(ColorAttribute.createDiffuse(Color.RED)),
        	material,
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
        btRigidBody body = new btRigidBody(constructionInfo);
        body.setActivationState(int newState);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        
        instance = new BulletInstance(model, body);
    }
    
    public Babo setForce(Vector3 f) {
        force.set(f.x, f.y, f.z);
        return this;
    }
    
    public Vector3 getForce() {
        return force.cpy();
    }
    
    public void update() {
        if( !force.isZero() ) {
        	force.scl(Gdx.graphics.getDeltaTime());
        	System.out.println("Bouge: "+force.toString());
            instance.body.applyCentralForce(force);
        }
    }
}