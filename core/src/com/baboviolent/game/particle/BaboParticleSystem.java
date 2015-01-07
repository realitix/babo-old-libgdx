package com.baboviolent.game.particle;


import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.particle.batches.BaboParticleBatch;
import com.baboviolent.game.particle.batches.BatchSpecific1;
import com.baboviolent.game.particle.effects.BaboParticleEffect;
import com.baboviolent.game.particle.effects.Smoke1Effect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class BaboParticleSystem {
	private ObjectMap<String, PoolParticle> pools;
	private Array<BaboParticleBatch> batches;

	public BaboParticleSystem(Camera camera) {
		initSystem(camera);
	}
	
	/**
	 * Initialise les batches de tous les types
	 * et ensuite les pools
	 */
	private void initSystem(Camera camera) {
		/*
		 * Initialisation des batches
		 */
		String p = BaboViolentGame.PATH_PARTICLES;
		batches = new Array<BaboParticleBatch>();
		
		BaboParticleBatch batch1 = new BaboParticleBatch(
				camera, 
				new Texture(Gdx.files.internal(p+"smoke2.png")));
		
		BaboParticleBatch batch2 = new BatchSpecific1(
				camera, 
				new Texture(Gdx.files.internal(p+"smoke2.png")));
		
		batches.addAll(batch1, batch2);
		
		/*
		 * Initialisation des pools
		 */
		pools = new ObjectMap<String, PoolParticle>();
		pools.put(Smoke1Effect.NAME, new PoolParticle(new Smoke1Effect(batch1), this));
	}
	
	/**
	 * Start particle effect
	 */
	public void start(String name, Matrix4 transform) {
		BaboParticleEffect effect = pools.get(name).obtain();
    	effect.init();
    	effect.reset();
        effect.start();
        effect.setTransform(transform);
        effect.getBatch().addEffect(effect);
	}
	
	public void render(ModelBatch modelBatch, Environment environment) {
		updatePools();
		
		for( int i = 0; i < batches.size; i++ ) {
			if( batches.get(i).getEffects().size > 0 ) {
				batches.get(i).begin();
				for(int j = 0; j < batches.get(i).getEffects().size; j++) {
					batches.get(i).getEffects().get(j).update();
					batches.get(i).getEffects().get(j).draw();
				}
				batches.get(i).end();
				modelBatch.render(batches.get(i), environment);
			}
		}
    }
	
	private void updatePools() {
		for (ObjectMap.Entry<String, PoolParticle> e : pools.entries()) {
			e.value.update();
        }
	}
	
	public void remove(BaboParticleEffect effect) {
		effect.getBatch().removeEffect(effect);
	}
}
