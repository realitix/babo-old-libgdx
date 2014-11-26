package com.baboviolent.game.gameobject;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.loader.BaboModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class GameObject {
	protected String name;
    protected float friction;
    protected float rollingFriction;
    protected float linearDamping;
    protected float angularDamping;
    protected float restitution;
    protected float mass;
	protected BulletInstance instance;
	protected btCollisionShape shape;
	protected btRigidBody.btRigidBodyConstructionInfo ci;
	
	public GameObject() {
	}
	
	protected void initInstance() {
        Model model = BaboModelLoader.getModel(name);
        shape = Utils.convexHullShapeFromModel(model);
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
    
    public BulletInstance getInstance() {
        return instance;
    }
}