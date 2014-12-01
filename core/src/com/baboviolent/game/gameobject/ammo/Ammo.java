package com.baboviolent.game.gameobject.ammo;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.loader.BaboModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Ammo extends GameObject {
	protected expireTime; // temps avant expiration en millisecondes
	
	public Ammo() {
		super();
	}
	
	protected void init() {
	    super.initModel();
	    shape = Utils.convexHullShapeFromModel(model);
	    body = initBody(shape);
	    
	    // Empeche la balle de traverser les murs
        // http://www.bulletphysics.org/mediawiki-1.5.8/index.php?title=Anti_tunneling_by_Motion_Clamping
        btCollisionShape = body.getcollisionShape();
        float radius;
        shape.getBoundingSphere(new Vector3(), radius);
        body.setCcdMotionThreshold(radius);
        body.setCcdSweptSphereRadius(radius/2);
	}
	
	public BulletInstance getInstance() {
        return new BulletInstance(model, body).setExpire(expireTime);
	}
}