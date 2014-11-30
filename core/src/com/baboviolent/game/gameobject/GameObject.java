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
	protected Quaternion tmpQ2 = new Quaternion();
	
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
    	tmpM.getRotation(tmpQ, true);
    	tmpV3.sub(t);
    	tmpV2.set(tmpV3.x, tmpV3.z);
    	float targetAngle = tmpV2.angle(); // 0 -> droite, 90 -> bas, 180 -> gauche, 270 -> haut
    	
    	// tmpQ.getAngleAround(up) // 0 -> gauche, 90 -> bas, 180 -> droite, 270 -> haut Le quart haut gauche par en vrille et renvoie n'importe quoi
    	// A partir de 240 = 120 et ensuite décroit jusqu'a 0
    	// Si l'axe y du quaternion = -1 on soustrait l'angle a 360
    	
    	Vector3 test2 = new Vector3();
    	float test = tmpQ.getAxisAngle(test2);
    	
    	float currentAngle = test;
    	if( test2.y < 0 ) {
    		currentAngle = 360 - currentAngle;
    	}
    	currentAngle = (360 - currentAngle + 180) %360;
    	System.out.println("angle : "+currentAngle);
    	
    	/**
    	 * Si on est dirigé vers la cible, on stoppe le mouvement
    	 */
    	if( Math.abs(targetAngle - currentAngle) < 10 ) {
    		instance.body.clearForces();
    		instance.body.applyTorqueImpulse(instance.body.getAngularVelocity().cpy().scl(-1000));
    	}
    	/**
    	 * Si on est pas dirigé vers la cible,
    	 * Si la cible est plus vers la droite, on envoie une impulsion vers la droite, sinon vers la gauche
    	 * La fonction est appelé constamment, il ne faut pas envoyer plusieurs impulsions donc
    	 * Si la cible est vers la droite et que le vitesse angulaire est vers la droite, on ne fait rien
    	 * Si la cible est vers la gauche et que le vitesse angulaire est vers la droite, on envoie une forte impulsion pour la faire aller de l'autre coté
    	 * Pour savoir de quel côté est l'angle, on met l'origine du cercle sur le currentAngle,
    	 * On place le targetAngle sur cette origine (on ajoute ce qu'on a ajouté au currentAngle pour devenir l'origine)
    	 * On fait targetAngle - 180, si c'est positif = gauche, si c'est négatif = doite
    	 */
    	else {
    		
    		float impulse = 0;
    		if( targetAngle - currentAngle < 0 ) {
    			impulse *= -1;
    		}
    		instance.body.applyTorqueImpulse(new Vector3(0, impulse, 0));
    	}
    }
    
    // Algo: http://gamedev.stackexchange.com/questions/81301/what-torque-should-i-apply-in-bullet-to-maintain-a-vertical-orientation
    public void lookAt3(Vector3 t) {
    	instance.body.getMotionState().getWorldTransform(tmpM);
    	Quaternion targetOrientation = new Quaternion().set(up, 100);
    	Quaternion currentOrientation = new Quaternion();
    	tmpM.getRotation(currentOrientation);
    	
    	System.out.println("x="+currentOrientation.x+" y="+currentOrientation.y+" z="+currentOrientation.z);
    	System.out.println("x="+targetOrientation.x+" y="+targetOrientation.y+" z="+targetOrientation.z);
    	
    	// Inverse currentOriantation
    	Quaternion currentOrientationInverse = new Quaternion(-currentOrientation.x, -currentOrientation.y, -currentOrientation.z, currentOrientation.w);
    	Quaternion deltaOrientation = new Quaternion();
    	deltaOrientation = targetOrientation.cpy();
    	
    	// Scale with current
    	deltaOrientation.x *= currentOrientationInverse.x;
    	deltaOrientation.y *= currentOrientationInverse.y;
    	deltaOrientation.z *= currentOrientationInverse.z;
    	deltaOrientation.w *= currentOrientationInverse.w;
    	//deltaOrientation.mul(currentOrientationInverse);

    	Vector3 deltaEuler = quaternionToEulerXYZ(deltaOrientation);
    	Vector3 torqueToApply = deltaEuler.cpy().scl(-100);
    	instance.body.applyTorque(torqueToApply);
    }
    
    private Vector3 quaternionToEulerXYZ(Quaternion q) {
       float w = q.w;
       float x = q.x;
       float y = q.y;
       float z = q.z;
       double sqw = w*w; double sqx = x*x; double sqy = y*y; double sqz = z*z;
       Vector3 euler = new Vector3();
       euler.z = (float) Math.atan2(2.0 * (x*y + z*w), (sqx - sqy - sqz + sqw));
       euler.x = (float) Math.atan2(2.0 * (y*z + x*w),(-sqx - sqy + sqz + sqw));
       euler.y = (float) Math.asin(-2.0 * (x*z - y*w));
       return euler;
    }

    public void lookAt2(Vector3 t) {
    	
    	/*instance.body.getMotionState().getWorldTransform(tmpM);
    	tmpM.getTranslation(tmpV3);
    	tmpM.getRotation(tmpQ);
    	tmpV3.sub(t);
    	tmpV2.set(tmpV3.x, tmpV3.z);
    	float angle = -tmpV2.angle() + 180;
    	//System.out.println("Rotation: "+(angle - tmpQ.getAngleAround(up)));
    	System.out.println("Angle avant: "+tmpQ.getAngleAround(up));
    	//System.out.println("Angle apres: "+angle);
    	
    	tmpQ2.idt().set(up, (angle - tmpQ.getAngle()));
    	//tmpQ2.idt().set(up, angle);
    	instance.body.getWorldTransform().set(tmpQ2);*/
    	//tmpM.rotate(tmpQ2);
    	//instance.body.proceedToTransform(tmpM);
    	//instance.body.setWorldTransform(tmpM);
    	//tmpM.setToRotation(up, tmpV2.angle());
    	/*Matrix4 test = new Matrix4(tmpM.getTranslation(tmpV3), tmpQ2, new Vector3(1,1,1));
    	instance.body.setCenterOfMassTransform(test);
    	instance.body.clearForces();
    	instance.body.*/
    	//instance.body.setCenterOfMassTransform(tmpM);*/
    }
}