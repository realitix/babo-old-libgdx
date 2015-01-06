package com.baboviolent.game.gameobject.weapon;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.ammo.SmallCalibre;
import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.particle.BaboParticleSystem;
import com.baboviolent.game.particle.PoolParticle;
import com.baboviolent.game.particle.effect.Smoke1Effect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.PolarAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.TimeUtils;

public class Shotgun extends Weapon {
	
	public Shotgun(final Babo babo, final BulletWorld world, final BaboParticleSystem particle) {
		super(babo, world, particle);
		name = "Shotgun";
		impulse = 50000;
		rotateImpulse = 200000;
		frequency = 1000;
		
	    friction = 0.1f;
        rollingFriction = 0.1f;
        linearDamping = 0.1f;
        angularDamping = 0.1f;
        restitution = 0.1f;
        mass = 10;
        
        super.initInstance();
	}
	
	public void shoot(Vector3 target) {
		if( TimeUtils.millis() - lastShoot < frequency )
			return;

		// Applique la direction
		instance.body.getMotionState().getWorldTransform(tmpM);
		tmpM.getRotation(tmpQ);
		validQuaternion(tmpQ);
		
		// Decale tmpM pour etre au bout du fusil
		Quaternion decal = rotateQuaternion(tmpQ.cpy(), 353);
		tmpM.trn(decal.transform(tmpV3.set(1,0,0)).scl(130));
		
		// Le fusil tir plusieurs balles en meme temps
		int nbAmmos = 4;
		float angle = 10;
		Quaternion rotation = tmpQ.cpy();

		// Initialise la rotation
		rotateQuaternion(rotation, -nbAmmos/2*angle + 0.5f*angle);
        
        // Envoie les particules
    	for( int i = 0; i < nbAmmos; i++ ) {
			// Initialise la balle
    		SmallCalibre ammo = new SmallCalibre(this);
			BulletInstance a = ammo.getInstance();
			Matrix4 matrixAmmo = tmpM.cpy().trn(rotation.transform(tmpV3.set(1,0,0)).scl(30));
			a.body.setCenterOfMassTransform(matrixAmmo);
			world.add(a);
			
			// Envoie la balle
			rotation.transform(tmpV3.set(1,0,0));
			tmpV3.nor().scl(impulse);
	    	a.body.applyCentralImpulse(tmpV3);
			//a.body.applyCentralImpulse(tmpV3.set(1,0,0).mul(rotation).nor().scl(impulse));
	        
	        // On incremente l'angle
	    	rotateQuaternion(rotation, angle);
	    	
	    	// On ajoute la balle au contact listener
	    	BulletContactListener.addObject(ammo);
		}
    	
    	
    	// Envoie la particule
    	particle.start(Smoke1Effect.NAME, tmpM);
    	/*ParticleEffect effect = particule.obtain();
    	effect.init();
    	effect.reset();
        effect.start();
        effect.setTransform(tmpM);*/
        /*DynamicsInfluencer influencer = effect.getControllers().get(0).findInfluencer(DynamicsInfluencer.class);
        PolarAcceleration modifier = (PolarAcceleration) influencer.velocities.get(0);
        float rotate = getAngleFromQuaternion(tmpQ);
        modifier.thetaValue.setHighMin((-10 + rotate));
        modifier.thetaValue.setHighMax((10 + rotate));*/
        //ParticleSystem.get().add(effect);
    	
    	// On creer la force inverse
    	//instance.body.applyCentralImpulse(tmpV3.scl(-100));
    	
    	// On enregistre la date du tir
    	lastShoot = TimeUtils.millis();
	}
	
	/**
	 * Si un Quaternion est � l'envers, on le remet dans le bon sens
	 */
	private Quaternion validQuaternion(Quaternion q) {
		Vector3 currentAngleAxis = new Vector3();
    	float currentAngle = q.getAxisAngle(currentAngleAxis);
    	
    	if( currentAngleAxis.y < 0 ) {
    		q.set(up, 360 - currentAngle);
    	}
    	
    	return q;
	}
	
	/**
	 * Tourne un Quaternion en prenant en compte les valeur trop petite ou trop grande
	 */
	private Quaternion rotateQuaternion(Quaternion q, float angle) {
		float f = q.getAngleAround(up) + angle;
    	f = (f < 0) ? 360 + f : f;
    	f = (f > 360) ? f%360 : f;
		q.set(up, f);
    	return q;
	}
	
	public float getAngleFromQuaternion(Quaternion q) {
		Vector3 currentAngleAxis = new Vector3();
    	float currentAngle = q.getAxisAngle(currentAngleAxis);
    	
    	if( currentAngleAxis.y < 0 ) {
    		currentAngle = 360 - currentAngle;
    	}
    	
    	return (360 - currentAngle ) % 360;
	}
}