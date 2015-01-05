package com.baboviolent.game.particle.effect;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.utils.Array;

public class BaboParticleEffect extends ParticleEffect {
	protected Array<ParticleController> baboControllers;
	protected BillboardParticleBatch batch;
	protected Texture texture;
	
	public BaboParticleEffect(BillboardParticleBatch batch, Texture texture) {
		super();
		baboControllers = new Array<ParticleController>();
		this.batch = batch;
		this.texture = texture;
	}
	
	protected void configure() {
		this.getControllers().addAll(baboControllers);
	}
}
