package com.baboviolent.game.particle.effect;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.utils.Array;

public class BaboParticleEffect extends ParticleEffect {
	protected int type;
	protected String name;
	
	public BaboParticleEffect() {
		super();
	}
	
	public BaboParticleEffect(BaboParticleEffect effect) {
		super(effect);
		type = effect.getType();
		name = effect.getName();
	}

	public String getName() {
		return name;
	}
	
	public int getType() {
		return type;
	}
	
	public BaboParticleEffect copy() {
		return new BaboParticleEffect(this);
	}
}
