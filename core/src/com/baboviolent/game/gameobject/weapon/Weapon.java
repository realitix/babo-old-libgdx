package com.baboviolent.game.gameobject.weapon;

import java.util.Random;

import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
import com.badlogic.gdx.math.Vector3;

public class Weapon extends GameObject {
	protected final Babo babo; // Babo tenant l'arme
	protected float impulse; // Puissance de la balle
	protected float frequency; // Temps en millisecond entre deux tirs
	protected float rotateImpulse; // Impulsion pour faire tourner l'arme
	protected float distanceShoot; // Distance du tir
	protected float power; // Puissance du tir
	protected int nbCartridge; // Nombre de cartouche dans le chargeur
	protected int currentNbCartridge;
	protected int timeReload; // Temps en millisecond pour recharger
	protected long lastShoot;
	protected long lastReload;
	
	public Weapon(final Babo b) {
		super();
		type = GameObject.TYPE_WEAPON;
		babo = b;
		
		friction = 0.1f;
        rollingFriction = 0.1f;
        linearDamping = 0.1f;
        angularDamping = 0.1f;
        restitution = 0.1f;
        mass = 1;
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
    	float ceil = 3;
    	float diffAngle = Math.abs(targetAngle - currentAngle);
    	if( diffAngle < ceil || diffAngle > 360 - ceil ) {
    		instance.body.clearForces();
    		instance.body.setAngularVelocity(Vector3.Zero);
    	}
    	else {
    		float av = instance.body.getAngularVelocity().y;
    		float velocity = 5;
    		//float velocity = (Math.abs(targetAngle - currentAngle)%360)/5;
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
	
	protected void updateHud() {
		if( babo.getHud() != null ) {
			babo.getHud().setCartridge(currentNbCartridge);
			if( currentNbCartridge <= 0 ) {
				babo.getHud().reload(timeReload);
			}
		}
	}
	
	public Babo getBabo() {
		return babo;
	}
	
	public void shoot(Vector3 target) {
	}
}