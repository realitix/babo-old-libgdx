package com.baboviolent.game.effect.particle.batches;

import com.baboviolent.game.effect.particle.effects.BaboParticleEffect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader.AlignMode;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.utils.Array;

public class BaboParticleBatch extends BillboardParticleBatch {	
	private Array<BaboParticleEffect> effects;
	
	public BaboParticleBatch(Camera camera, Texture texture) {
		super(AlignMode.Screen, true, 100);
		effects = new Array<BaboParticleEffect>();
		this.setCamera(camera);
		this.setTexture(texture);
	}
	
	@Override
	protected Renderable allocRenderable() {
		Renderable renderable = super.allocRenderable();
		renderable.material = null;
		renderable.material = new Material(	new BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA, 1),
				new DepthTestAttribute(GL20.GL_LEQUAL, true),
				TextureAttribute.createDiffuse(texture));
		return renderable;
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
