package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.baboviolent.game.effect.particle.influencers.RotationInfluencer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
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
	
	@Override
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
		rotateTexture(getAngleFromQuaternion(tmpQ) - 90);
	}
	
	/**
	 * Ne recupere que le premier controleur
	 * Suffisant pour l'instant
	 */
	protected void rotateTexture(float angle) {
		for( int i = 0; i < getControllers().size; i++ ) {
			ParticleController pc = getControllers().get(i);
			
			// Si l'influencer rotation n'existe pas on le cree
			RotationInfluencer influencer = pc.findInfluencer(RotationInfluencer.class);
			if( influencer == null ) {
				influencer = new RotationInfluencer();
				pc.influencers.add(influencer);
			}
			influencer.value.setHigh(angle%360);
		}
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
	
	/*
	 * Correct the initial comportment to add transform
	 */
	@Override
	public BoundingBox getBoundingBox () {
		Matrix4 m = new Matrix4();
		getControllers().get(0).getTransform(m);
		return super.getBoundingBox().mul(m);
	}
	
	public void setWidth(float width) {}
	public void setNormal(int normal) {}
	public void setNormalRay(Vector3 normalRay) {}
}
