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
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.PoolParticle;
import com.baboviolent.game.effect.particle.effects.Bullet1Effect;
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
		impulse = 50000;
		rotateImpulse = 200000;
		frequency = 1000;
		distanceShoot = 1000;
		power = 20;
		
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
		int nbAmmos = 5;
		float angle = 5;
		float angleMin = 3;
		float angleMax = 8;
		Quaternion rotation = tmpQ.cpy();

		// Initialise la rotation
		//rotateQuaternion(rotation, -nbAmmos/2*angle + 0.5f*angle);
        
        // Envoie les particules
		tmpM.rotate(up, -nbAmmos/2*angle);
    	for( int i = 0; i < nbAmmos; i++ ) {
			tmpM.rotate(up, angle);
			
			Vector3 from = new Vector3();
			Vector3 to = new Vector3();
			
			tmpM.getTranslation(from);
			tmpM.getRotation(rotation).transform(tmpV3.set(1,0,0));
			tmpV3.nor().scl(distanceShoot);
			to.set(from).add(tmpV3);
			
			BulletRayResult result = world.getRayResult(from, to);
			int normal = 0;
			Vector3 normalRay = null;
			if( result != null ) {
				from.set(result.getStartRay());
				to.set(result.getEndRay());
				if( !result.isMap() && result.getObject().getType() == GameObject.TYPE_BABO ) {
					((Babo)result.getObject())
						.setLastShooter(this.getBabo())
						.hit(power);
				}
				
				normal = result.getNormalRayToRefactor();
				normalRay = result.getNormalRay();
			}
			
			effectSystem.get(Shoot1.NAME).start(tmpM, from, to, normal, normalRay);
		}
    	
    	// On creer la force inverse
    	//instance.body.applyCentralImpulse(tmpV3.scl(-100));
    	
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