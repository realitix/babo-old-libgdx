package com.baboviolent.game.particle.effects;

import com.baboviolent.game.particle.batches.BaboParticleBatch;
import com.baboviolent.game.particle.influencers.RotationInfluencer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class BaboParticleEffect extends ParticleEffect {
	protected BaboParticleBatch batch;
	protected String name;
	protected boolean textureFaceDirection;
	
	public BaboParticleEffect(BaboParticleBatch batch) {
		super();
		this.batch = batch;
	}
	
	public BaboParticleEffect(BaboParticleEffect effect) {
		super(effect);
		batch = effect.getBatch();
		name = effect.getName();
		textureFaceDirection = effect.getTextureFaceDirection();
	}

	public String getName() {
		return name;
	}
	
	public BaboParticleBatch getBatch() {
		return batch;
	}
	
	public boolean getTextureFaceDirection() {
		return textureFaceDirection;
	}
	
	public BaboParticleEffect copy() {
		return new BaboParticleEffect(this);
	}
	
	protected float getAngleFromQuaternion(Quaternion q) {
		Vector3 currentAngleAxis = new Vector3();
    	float currentAngle = q.getAxisAngle(currentAngleAxis);
    	
    	if( currentAngleAxis.y < 0 ) {
    		currentAngle = 360 - currentAngle;
    	}
    	
    	return (360 - currentAngle ) % 360;
	}
	
	/**
	 * Force la texture  prendre la direction actuelle
	 */
	protected void textureFaceDirection() {
		Quaternion tmpQ = new Quaternion();
		this.getControllers().get(0).transform.getRotation(tmpQ);
		rotateTexture(getAngleFromQuaternion(tmpQ));
	}
	
	/**
	 * Ne recupere que le premier controleur
	 * Suffisant pour l'instant
	 */
	protected void rotateTexture(float angle) {
		// Si l'influencer rotation n'existe pas on le cree
		RotationInfluencer influencer = this.getControllers().get(0).findInfluencer(RotationInfluencer.class);
		if( influencer == null ) {
			influencer = new RotationInfluencer();
			this.getControllers().get(0).influencers.add(influencer);
		}
		influencer.value.setHigh(angle);
	}
	
	/**
	 * Cette particule est toujours dirigee vers la direction
	 */
	@Override
	public void setTransform(Matrix4 transform) {
		super.setTransform(transform);
		if( textureFaceDirection ) {
			textureFaceDirection();
		}
	}
	
	public void setWidth(float width) {
	}
}
