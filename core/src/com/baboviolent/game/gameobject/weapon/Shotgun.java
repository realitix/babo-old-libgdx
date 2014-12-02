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
		
		// Vecteur de direction
		tmpV3.set(target);
		tmpV3.y = 0;
		
		// Applique la direction
		instance.body.getMotionState().getWorldTransform(tmpM);
		tmpM.getTranslation(tmpV32);
		tmpV32.y = 0;
		tmpV3.sub(tmpV32);
		
		// Normalise le vecteur
		float xz = tmpV3.x + tmpV3.z;
		tmpV3.x /= xz;
		tmpV3.z /= xz;
		
		// Initialise la balle
		BulletInstance a = ammo.getInstance();
		System.out.println(tmpV3);
		tmpM.translate(tmpV3.scl(200));
		// Voir avec Matrix4.trn
		
    	a.body.setCenterOfMassTransform(tmpM);
		world.add(a);
		
		// Envoie la balle
    	//a.body.applyCentralImpulse(tmpV3.nor().scl(impulse));
    	
    	// Envoie la particule
    	ParticuleEffect effect = particule.obtain();
    	effect.init();
        effect.start();
        effect.setTransform(tmpM);
        ParticleSystem.get().add(effect);
    	
    	// On créer la force inverse
    	instance.body.applyCentralImpulse(tmpV3.scl(-100));
    	
    	// On enregistre la date du tir
    	lastShoot = TimeUtils.millis();
	}
	
	public void stopShoot() {
		
	}
}