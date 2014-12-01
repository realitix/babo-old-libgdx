package com.baboviolent.game.gameobject.weapon;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.ammo.SmallCalibre;
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
import com.badlogic.gdx.utils.TimeUtils;

public class Shotgun extends Weapon {
	
	public Shotgun(final BulletWorld world) {
		super(world);
		name = "Shotgun";
		type = GameObject.TYPE_WEAPON;
		ammo = new SmallCalibre();
		impulse = 3000;
		frequency = 100;
		
	    friction = 0;
        rollingFriction = 0;
        linearDamping = 0;
        angularDamping = 0;
        restitution = 0;
        mass = 1;
        super.initInstance();
	}
	
	public void shoot(Vector3 target) {
		if( TimeUtils.millis() - lastShoot < frequency )
			return;
		lastShoot = TimeUtils.millis();
		
		BulletInstance a = ammo.getInstance();
		world.add(a);
		instance.body.getMotionState().getWorldTransform(tmpM);
    	a.body.setCenterOfMassTransform(tmpM);
    	target.y = 0;
    	Vector3 test = new Vector3();
    	test.set(target);
    	test.sub(a.body.getCenterOfMassPosition());
    	a.body.applyCentralImpulse(test.nor().scl(impulse));
    	
    	// On créer la force inverse
    	instance.body.applyCentralImpulse(test.scl(-100));
	}
	
	public void stopShoot() {
		
	}
}