package com.baboviolent.game.effect.particle.batches;

import com.baboviolent.game.effect.particle.effects.BaboParticleEffect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.utils.Array;

public class BaboParticleBatch extends BillboardParticleBatch {	
	private Array<BaboParticleEffect> effects;
	
	public BaboParticleBatch(Camera camera, Texture texture) {
		super();
		effects = new Array<BaboParticleEffect>();
		this.setCamera(camera);
		this.setTexture(texture);
	}
	
	public void addEffect(BaboParticleEffect effect) {
		effects.add(effect);
	}
	
	public void removeEffect(BaboParticleEffect effect) {
		effects.removeValue(effect, true);
	}
	
	public boolean hasEffect(BaboParticleEffect effect) {
		return effects.contains(effect, true);
	}
	
	public Array<BaboParticleEffect> getEffects() {
		return effects;
	}
}
