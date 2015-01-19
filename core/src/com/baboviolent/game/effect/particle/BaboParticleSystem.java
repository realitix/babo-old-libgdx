package com.baboviolent.game.effect.particle;


import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.baboviolent.game.effect.particle.batches.BatchSpecific1;
import com.baboviolent.game.effect.particle.batches.BatchSpecific2;
import com.baboviolent.game.effect.particle.effects.BaboParticleEffect;
import com.baboviolent.game.effect.particle.effects.Collision1Effect;
import com.baboviolent.game.effect.particle.effects.MuzzleFlash1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke2Effect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class BaboParticleSystem {
	private ObjectMap<String, PoolParticle> pools;
	private Array<BaboParticleBatch> batches;
	private Camera camera;

	public BaboParticleSystem(Camera camera) {
		this.camera = camera;
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
		
		BaboParticleBatch batch1 = new BatchSpecific1(
				camera, 
				new Texture(Gdx.files.internal(p+"smoke2.png")));
		
		BaboParticleBatch batch2 = new BatchSpecific1(
				camera, 
				new Texture(Gdx.files.internal(p+"smoke1.png")));
		
		BaboParticleBatch batch3 = new BatchSpecific2(
				camera, 
				new Texture(Gdx.files.internal(p+"nuzzleFlash.png")));
		
		BaboParticleBatch batch4 = new BatchSpecific2(
				camera, 
				new Texture(Gdx.files.internal(p+"shotGlow.png")));
		
		batches.addAll(batch1, batch2, batch3, batch4);
		
		/*
		 * Initialisation des pools
		 */
		pools = new ObjectMap<String, PoolParticle>();
		pools.put(Smoke1Effect.NAME, new PoolParticle(new Smoke1Effect(batch1), this));
		pools.put(Smoke2Effect.NAME, new PoolParticle(new Smoke2Effect(batch2), this));
		pools.put(MuzzleFlash1Effect.NAME, new PoolParticle(new MuzzleFlash1Effect(batch3), this));
		pools.put(Collision1Effect.NAME, new PoolParticle(new Collision1Effect(batch4), this));
	}
	
	public void start(String name, Matrix4 transform) {
		start(name, transform, 0, 0, null);
	}
	
	public void startWithWidth(String name, Matrix4 transform, float width) {
		start(name, transform, width, 0, null);
	}
	
	public void startWithNormal(String name, Matrix4 transform, int normal) {
		start(name, transform, 0, normal, null);
	}
	
	public void startWithNormal(String name, Matrix4 transform, Vector3 normal) {
		start(name, transform, 0, 0, normal);
	}
	
	/**
	 * Start particle effect
	 * @param name Le nom de la particule a lance
	 * @param transfrom La position
	 * @param width La largeur de l'emission si implemente dans la particule
	 */
	public void start(String name, Matrix4 transform, float width, int normal, Vector3 normalRay) {
		BaboParticleEffect effect = pools.get(name).obtain();
		effect.setTransform(transform);
		
		if( width != 0 ) {
			effect.setWidth(width);
		}
		
		effect.setNormalRay(normalRay);
		
		// L'effet doit etre initialise avant d'etre valide
		effect.init();
		if( validEffect(effect) ) {
    		effect.reset();
        	effect.start();
        	effect.getBatch().addEffect(effect);
		}
	}
	
	/**
	 * Affiche les particules si visible a l'ecran
	 * @param modelBatch
	 * @param environment
	 */
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
	
	/**
	 * Permet de n'afficher que les effets visibles a l'ecran
	 * Optimisation
	 */
	private boolean validEffect(BaboParticleEffect effect) {
		if ( camera.frustum.boundsInFrustum(effect.getBoundingBox()) ) {
			return true;
		}
		
		return false;
	}
}
