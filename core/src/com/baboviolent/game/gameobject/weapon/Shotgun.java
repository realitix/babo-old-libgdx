package com.baboviolent.game.gameobject.weapon;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletCollector;
import com.baboviolent.game.bullet.BulletContactListener;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletRayResult;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.group.Shoot1;
import com.baboviolent.game.effect.light.effects.Light1Effect;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.PoolParticle;
import com.baboviolent.game.effect.particle.effects.Smoke1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke2Effect;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.ammo.SmallCalibre;
import com.baboviolent.game.loader.TextureLoader;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.TimeUtils;

public class Shotgun extends Weapon {
	
	public Shotgun(final Babo babo, final BulletWorld world, final BaboEffectSystem effectSystem) {
		super(babo, world, effectSystem);
		name = "Shotgun";
		impulse = 100;
		rotateImpulse = 200000;
		frequency = 1000;
		distanceShoot = 10;
		power = 20;
	    
        
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
		tmpM.trn(decal.transform(tmpV3.set(1,0,0)).scl(1.3f));
		Matrix4 tmpMBase = tmpM.cpy();
		
		// Le fusil tir plusieurs balles en meme temps
		int nbAmmos = 5;
		float angle = 5;
		Quaternion rotation = tmpQ.cpy();

		// Initialise la rotation
		//rotateQuaternion(rotation, -nbAmmos/2*angle + 0.5f*angle);
        
        // Envoie les particules
		tmpM.rotate(up, -nbAmmos/2*angle);
		float tmpf = 0;
    	for( int i = 0; i < nbAmmos; i++ ) {
    		tmpf = angle - tmpf; // On reintialise tmpf
    		tmpf += angle + MathUtils.random(-angle, angle);
			tmpM.rotate(up, tmpf);
			
			Vector3 from = new Vector3();
			Vector3 to = new Vector3();
			
			tmpM.getTranslation(from);
			tmpM.getRotation(rotation).transform(tmpV3.set(1,0,0));
			tmpV3.nor().scl(distanceShoot);
			to.set(from).add(tmpV3);
			
			BulletRayResult result = world.getRayResult(from, to);
			Vector3 normalRay = null;
			if( result != null ) {
				from.set(result.getStartRay());
				to.set(result.getEndRay());
				if( !result.isMap() ) {
					Babo targetBabo = null;
					switch( result.getObject().getType() ) {
						case GameObject.TYPE_BABO:
							targetBabo = ((Babo)result.getObject());
							break;
						case GameObject.TYPE_WEAPON:
							targetBabo = ((Weapon)result.getObject()).getBabo();
					}
					
					if( targetBabo != null ) {
						targetBabo.setLastShooter(this.getBabo())
						.hit(power);
					}
				}
				
				normalRay = result.getNormalRay();
			}
			effectSystem.get(Shoot1.NAME).start(tmpM, from, to, normalRay);
		}
    	effectSystem.get(Shoot1.NAME).startUnique(tmpMBase);
    	
    	// On creer la force inverse
    	tmpMBase.getTranslation(tmpV3);
    	instance.body.applyCentralImpulse(tmpV3.sub(target).nor().scl(impulse));
    	
    	// On enregistre la date du tir
    	lastShoot = TimeUtils.millis();
	}
	
	/**
	 * Si un Quaternion est a l'envers, on le remet dans le bon sens
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