package com.baboviolent.game.gameobject;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.loader.BaboModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class GameObject {
    public static final int TYPE_WEAPON = 1;
    public static final int TYPE_BABO = 2;
    public static final int TYPE_AMMO = 3;
    
	protected String name;
	protected int type;
    protected float friction;
    protected float rollingFriction;
    protected float linearDamping;
    protected float angularDamping;
    protected float restitution;
    protected float mass;
    protected Model model;
	protected BulletInstance instance;
	protected btCollisionShape shape;
	protected btRigidBody.btRigidBodyConstructionInfo ci;
	protected btRigidBody body;
	protected Vector3 tmpV3 = new Vector3();
	protected Vector2 tmpV2 = new Vector2();
	protected Vector3 up = new Vector3(0,1,0);
	protected Matrix4 tmpM = new Matrix4();
	protected Quaternion tmpQ = new Quaternion();
	
	public GameObject() {
	}
	
	protected void initModel() {
	    model = BaboModelLoader.getModel(name);
	}
	
	protected void initInstance() {
        initModel(); 
        shape = Utils.convexHullShapeFromModel(model);
        btRigidBody body = initBody(shape);
        
        if( type == TYPE_WEAPON ) {
            // On limite la rotation à l'axe y
            body.setAngularFactor(new Vector3(0,1,0));
        }
        
        instance = new BulletInstance(model, body);
    }
    
    protected btRigidBody initBody(btCollisionShape shape) {
        Vector3 localInertia = new Vector3();
        shape.calculateLocalInertia(mass, localInertia);
        ci = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        ci.setFriction(friction);
        ci.setRollingFriction(rollingFriction);
        ci.setLinearDamping(linearDamping);
        ci.setAngularDamping(angularDamping);
        ci.setRestitution(restitution);
        body = new btRigidBody(ci);
        return body;
    }
    
    public BulletInstance getInstance() {
        return instance;
    }
    
    public GameObject translate(Vector3 v) {
    	this.instance.body.translate(v);
    	this.instance.transform.translate(v);
    	return this;
    }
    
    public void lookAt(Vector3 t) {
    	instance.body.getMotionState().getWorldTransform(tmpM);
    	tmpM.getTranslation(tmpV3);
    	tmpV3.sub(t);
    	tmpV2.set(tmpV3.x, tmpV3.z);
    	tmpQ.idt().set(up, tmpV2.angle());
    	tmpM.rotate(tmpQ);
    	instance.body.setCenterOfMassTransform(tmpM);
    }
}