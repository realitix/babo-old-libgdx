package com.baboviolent.game.effect;

import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

public class BaboEffectSystem {
	private BaboParticleSystem particleSystem;
	
	public BaboEffectSystem(Camera camera) {
		particleSystem = new BaboParticleSystem(camera);
	}
	
	public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment) {
		particleSystem.render(modelBatch, environment);
	}
	
	public BaboParticleSystem getParticleSystem() {
		return particleSystem;
	}
}
