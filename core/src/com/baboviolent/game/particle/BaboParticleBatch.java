package com.baboviolent.game.particle;

import com.baboviolent.game.particle.effect.BaboParticleEffect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.utils.Array;

public class BaboParticleBatch extends BillboardParticleBatch {
	public static final int TYPE1 = 1;
	
	private Array<BaboParticleEffect> effects;
	private int type;
	
	public BaboParticleBatch(Camera camera, Texture texture, int type) {
		super();
		effects = new Array<BaboParticleEffect>();
		this.setCamera(camera);
		this.setTexture(texture);
		this.type = type;
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
	
	public int getType() {
		return type;
	}
}
