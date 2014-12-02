package com.baboviolent.game.gameobject.weapon;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.ammo.SmallCalibre;
import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.particle.PoolParticle;
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
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.PolarAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.TimeUtils;

public class Shotgun extends Weapon {
	
	private int test = 1;
	
	public Shotgun(final BulletWorld world, final PoolParticle particule) {
		super(world, particule);
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

		// Applique la direction
		instance.body.getMotionState().getWorldTransform(tmpM);
		tmpM.getRotation(tmpQ);
		
		// Initialise la balle
		BulletInstance a = ammo.getInstance();
		tmpM.trn(tmpQ.transform(tmpV3.set(1,0,0)).scl(130));
		
    	a.body.setCenterOfMassTransform(tmpM);
		world.add(a);
		
		// Envoie la balle
    	a.body.applyCentralImpulse(tmpV3.nor().scl(impulse));
    	
    	// Envoie la particule
    	ParticleEffect effect = particule.obtain();
    	effect.init();
    	effect.reset();
        effect.start();
        effect.setTransform(tmpM);
        DynamicsInfluencer influencer = effect.getControllers().get(0).findInfluencer(DynamicsInfluencer.class);
        PolarAcceleration modifier = (PolarAcceleration) influencer.velocities.get(0);
        
        Vector3 currentAngleAxis = new Vector3();
    	float currentAngle = tmpQ.getAxisAngle(currentAngleAxis);
    	if( currentAngleAxis.y < 0 ) {
    		currentAngle = 360 - currentAngle;
    	}
    	currentAngle = (360 - currentAngle ) % 360;
        modifier.thetaValue.setHigh(currentAngle);


        ParticleSystem.get().add(effect);
    	
    	// On créer la force inverse
    	//instance.body.applyCentralImpulse(tmpV3.scl(-100));
    	
    	// On enregistre la date du tir
    	lastShoot = TimeUtils.millis();
	}
	
	public void stopShoot() {
		
	}
}