package com.baboviolent.game.particle.effects;

import com.baboviolent.game.particle.batches.BaboParticleBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.utils.Array;

public class BaboParticleEffect extends ParticleEffect {
	protected BaboParticleBatch batch;
	protected String name;
	
	public BaboParticleEffect(BaboParticleBatch batch) {
		super();
		this.batch = batch;
	}
	
	public BaboParticleEffect(BaboParticleEffect effect) {
		super(effect);
		batch = effect.getBatch();
		name = effect.getName();
	}

	public String getName() {
		return name;
	}
	
	public BaboParticleBatch getBatch() {
		return batch;
	}
	
	public BaboParticleEffect copy() {
		return new BaboParticleEffect(this);
	}
	
	/**
	 * Ne recupere que le premier controleur
	 * Suffisant pour l'instant
	 */
	public void rotateTexture(float angle) {
		
	}
}
