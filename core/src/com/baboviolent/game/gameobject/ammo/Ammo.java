package com.baboviolent.game.gameobject.ammo;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.loader.BaboModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.SWIGTYPE_p_float;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.TimeUtils;

public class Ammo extends GameObject {
	protected long expireTime; // temps avant expiration en millisecondes
	
	public Ammo() {
		super();
	}
	
	protected void init() {
	    super.initModel();
	    shape = Utils.convexHullShapeFromModel(model);
	}
	
	public BulletInstance getInstance() {
		body = initBody(shape);
		
		// Empeche la balle de traverser les murs
        // http://www.bulletphysics.org/mediawiki-1.5.8/index.php?title=Anti_tunneling_by_Motion_Clamping
		float radius = 10;
        body.setCcdMotionThreshold(radius);
        body.setCcdSweptSphereRadius(radius/2);
        
        // Permet de detecter les contacts de la balle avec les babos
     	body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
     	body.setContactCallbackFilter(BulletContactListener.PLAYER_FLAG);
		
        return new BulletInstance(model, body).setExpire(TimeUtils.millis() + expireTime);
	}
}