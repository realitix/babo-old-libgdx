package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class BaboParticleEffect extends ParticleEffect {
	protected BaboParticleBatch batch;
	protected String name;
	protected final TextureAtlas atlas;
	
	public BaboParticleEffect(BaboParticleBatch batch, TextureAtlas atlas) {
		super();
		this.batch = batch;
		this.atlas = atlas;
	}
	
	public BaboParticleEffect(BaboParticleEffect effect) {
		super(effect);
		batch = effect.getBatch();
		name = effect.getName();
		atlas = effect.getAtlas();
	}

	public String getName() {
		return name;
	}
	
	public BaboParticleBatch getBatch() {
		return batch;
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
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
	 * Cette particule est toujours dirigee vers la direction
	 */
	@Override
	public void setTransform(Matrix4 transform) {
		super.setTransform(transform);
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
	public void setDamage(float damage) {}
}
