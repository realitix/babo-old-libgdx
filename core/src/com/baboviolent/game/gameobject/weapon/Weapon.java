package com.baboviolent.game.gameobject.weapon;

import java.util.Random;

import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.PoolParticle;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.ammo.Ammo;
import com.baboviolent.game.loader.BaboModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Weapon extends GameObject {
	protected final BulletWorld world;
	protected final Babo babo; // Babo tenant l'arme
	protected float impulse; // Puissance de la balle
	protected float frequency; // Temps en millisecond entre deux tirs
	protected float rotateImpulse; // Impulsion pour faire tourner l'arme
	protected float distanceShoot; // Distance du tir
	protected float power; // Puissance du tir
	protected long lastShoot;
	protected final BaboEffectSystem effectSystem;
	
	public Weapon(final Babo b, final BulletWorld w, final BaboEffectSystem e) {
		super();
		type = GameObject.TYPE_WEAPON;
		babo = b;
		world = w;
		effectSystem = e;
	}
	
	// Amelioration en utilisant seulement l'angular velocity
	public void lookAt(Vector3 t) {
		instance.body.getMotionState().getWorldTransform(tmpM);
    	tmpM.getTranslation(tmpV3);
    	tmpM.getRotation(tmpQ, true);
    	tmpV3.sub(t);
    	tmpV2.set(tmpV3.x, tmpV3.z);
    	float targetAngle = tmpV2.angle(); // 0 -> droite, 90 -> bas, 180 -> gauche, 270 -> haut
    	
    	Vector3 currentAngleAxis = new Vector3();
    	float currentAngle = tmpQ.getAxisAngle(currentAngleAxis);
    	if( currentAngleAxis.y < 0 ) {
    		currentAngle = 360 - currentAngle;
    	}
    	currentAngle = (360 - currentAngle + 180) %360;
    	
    	/**
    	 * Si on est dirige vers la cible, on stoppe le mouvement
    	 */
    	if( Math.abs(targetAngle - currentAngle) < 3 ) {
    		instance.body.clearForces();
    		instance.body.setAngularVelocity(new Vector3(0,0,0));
    	}
    	else {
    		float av = instance.body.getAngularVelocity().y;
    		//float velocity = 5;
    		float velocity = (Math.abs(targetAngle - currentAngle)%360)/5;
    		// On determine le sens de la target
    		boolean toLeft = false;
    		float targetAngleOrigin2 = targetAngle - currentAngle;
    		if( targetAngle < currentAngle )
    			targetAngleOrigin2 = targetAngle + (360 - currentAngle);
    		
    		if( targetAngleOrigin2 - 180 > 0 )
    			toLeft = true;
    		
    		if( toLeft && av <= velocity ) {
    			instance.body.setAngularVelocity(new Vector3(0, velocity, 0));
    		}
    		if( !toLeft && av >= -velocity) {
    			instance.body.setAngularVelocity(new Vector3(0, -velocity, 0));
    		}
    	}
	}
	
	public void eject() {
		Random rand = new Random();
        int max = (int)mass*100;
        int max2 = (int)mass*10;
        body.applyImpulse(
        	new Vector3(
        		rand.nextInt(max  + 1) - max/2,
        		rand.nextInt(max + 1),
        		rand.nextInt(max + 1) - max/2
        	),
        	new Vector3(
    			rand.nextInt(max2  + 1) - max2/2,
        		rand.nextInt(max2 + 1) - max2/2,
        		rand.nextInt(max2 + 1) - max2/2
        	)
        );
	}
	
	public Babo getBabo() {
		return babo;
	}
	
	public void shoot(Vector3 target) {
	}
}