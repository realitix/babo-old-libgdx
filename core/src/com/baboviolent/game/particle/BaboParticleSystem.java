package com.baboviolent.game.particle;


import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.particle.effect.BaboParticleEffect;
import com.baboviolent.game.particle.effect.Smoke1Effect;
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
		initBatches(camera);
		initPools();
	}
	
	/**
	 * Initialise les batches de tous les types
	 */
	private void initBatches(Camera camera) {
		String p = BaboViolentGame.PATH_PARTICLES;
		batches = new Array<BaboParticleBatch>();
		batches.add(new BaboParticleBatch(
				camera, 
				new Texture(Gdx.files.internal(p+"smoke2.png")), 
				BaboParticleBatch.TYPE1));
	}
	
	private void initPools() {
		pools = new ObjectMap<String, PoolParticle>();
		
		Smoke1Effect e1 = new Smoke1Effect();
		e1.configure(findBatchType(e1.getType()));
		pools.put(e1.getName(), new PoolParticle(e1, this));
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
        findBatchType(effect.getType()).addEffect(effect);
	}
	
	public void render(ModelBatch modelBatch, Environment environment) {
		updatePools();
		
		for( int i = 0; i < batches.size; i++ ) {
			if( batches.get(i).getEffects().size > 0 ) {
				batches.get(i).begin();
				for(int j = 0; j < batches.get(i).getEffects().size; j++) {
					batches.get(i).getEffects().get(i).update();
					batches.get(i).getEffects().get(i).draw();
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
	
	private BaboParticleBatch findBatchType(int type) {
		for( int i = 0; i < batches.size; i++ ) {
			if( batches.get(i).getType() == type ) {
				return batches.get(i);
			}
		}
		return null;
	}
	
	public void remove(BaboParticleEffect effect) {
		findBatchType(effect.getType()).removeEffect(effect);
	}
}
