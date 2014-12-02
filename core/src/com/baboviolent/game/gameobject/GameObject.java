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
	protected btRigidBody body;
	protected Vector3 tmpV3 = new Vector3();
	protected Vector3 tmpV32 = new Vector3();
	protected Vector2 tmpV2 = new Vector2();
	protected Vector3 up = new Vector3(0,1,0);
	protected Matrix4 tmpM = new Matrix4();
	protected Quaternion tmpQ = new Quaternion();
	protected Quaternion tmpQ2 = new Quaternion();
	
	protected boolean testImpulse = true;
	
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
        btRigidBody.btRigidBodyConstructionInfo ci = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        ci.setFriction(friction);
        ci.setRollingFriction(rollingFriction);
        ci.setLinearDamping(linearDamping);
        ci.setAngularDamping(angularDamping);
        ci.setRestitution(restitution);
        body = new btRigidBody(ci);
        
     	// On enleve le constructioninfo
     	ci.dispose();
        
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
    	
    	Vector3 currentAngleAxis = new Vector3();
    	float currentAngle = tmpQ.getAxisAngle(currentAngleAxis);
    	if( currentAngleAxis.y < 0 ) {
    		currentAngle = 360 - currentAngle;
    	}
    	currentAngle = (360 - currentAngle + 180) %360;
    	
    	/*if(testImpulse) {
    		instance.body.applyTorqueImpulse(new Vector3(0, -500, 0));
    		testImpulse = false;
    		
    	}*/
    	
    	/**
    	 * Si on est dirigé vers la cible, on stoppe le mouvement
    	 */
    	if( Math.abs(targetAngle - currentAngle) < 5 ) {
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
    	 * La vitesse angulaire est positive vers la gauche et negative vers la droite
    	 */
    	else {
    		float av = instance.body.getAngularVelocity().y;
    		float impulse = 10000;
    		float minVelocity = 1;
    		// On détermine le sens de la target
    		boolean toLeft = false;
    		float targetAngleOrigin2 = targetAngle - currentAngle;
    		if( targetAngle < currentAngle )
    			targetAngleOrigin2 = targetAngle + (360 - currentAngle);
    		
    		if( targetAngleOrigin2 - 180 > 0 )
    			toLeft = true;
    		
    		if( toLeft && av <= minVelocity ) {
    			instance.body.applyTorqueImpulse(new Vector3(0, impulse, 0));
    		}
    		if( !toLeft && av >= -minVelocity) {
    			instance.body.applyTorqueImpulse(new Vector3(0, -impulse, 0));
    		}
    	}
    }
}