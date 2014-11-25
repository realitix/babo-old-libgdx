package com.baboviolent.game.gameobject;

public class GameObject {
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
        String className = this.getClass().getSimpleName();
        Model model = BaboModelLoader.getModel(className);
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