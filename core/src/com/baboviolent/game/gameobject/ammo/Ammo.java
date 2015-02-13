package com.baboviolent.game.gameobject.ammo;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.instance.BulletInstance;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.weapon.Weapon;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.utils.TimeUtils;

public class Ammo extends GameObject {
	protected long expireTime; // temps avant expiration en millisecondes
	protected int power;
	protected final Weapon weapon;
	
	public Ammo(final Weapon w) {
		super();
		type = GameObject.TYPE_AMMO;
		weapon = w;
	}
	
	protected void init() {
	    super.initModel();
	    shape = Utils.convexHullShapeFromModel(model);
	    initInstance();
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public int getPower() {
		return power;
	}
	
	@Override
	public void initInstance() {
		body = initBody(shape);
		
		// Empeche la balle de traverser les murs
        // http://www.bulletphysics.org/mediawiki-1.5.8/index.php?title=Anti_tunneling_by_Motion_Clamping
		float radius = 10;
        body.setCcdMotionThreshold(radius);
        body.setCcdSweptSphereRadius(radius/2);
        
        // Permet de detecter les contacts de la balle avec les babos
     	body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
     	body.setContactCallbackFilter(BulletContactListener.BABO_FLAG);
		
        instance = new BulletInstance(model, body).setExpire(TimeUtils.millis() + expireTime);
	}
}