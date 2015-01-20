package com.baboviolent.game.effect.light;


import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.effect.light.effects.BaboLightEffect;
import com.baboviolent.game.effect.light.effects.Light1Effect;
import com.baboviolent.game.effect.particle.PoolParticle;
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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
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

public class BaboLightSystem {
	
	private ObjectMap<String, PoolLight> pools;
	private Environment environment;
	private Camera camera;
	private Array<BaboLightEffect> effects;
	
	public BaboLightSystem(Camera camera, Environment environment) {
		this.environment = environment;
		this.camera = camera;
		initSystem();
	}
	
	private void initSystem() {
		effects = new Array<BaboLightEffect>();
		pools = new ObjectMap<String, PoolLight>();
		pools.put(Light1Effect.NAME, new PoolLight(new Light1Effect(), this));
	}
	
	public void start(String name, Matrix4 transform) {
		BaboLightEffect effect = pools.get(name).obtain();
		effect.setTransform(transform);
		
		// L'effet doit etre initialise avant d'etre valide
		effect.init();
		if( validEffect(effect) ) {
    		effect.reset();
        	effect.start();
        	environment.add(effect.getLight());
        	effects.add(effect);
		}
	}

	private void updatePools() {
		for (ObjectMap.Entry<String, PoolLight> e : pools.entries()) {
			e.value.update();
        }
	}
	
	public void render() {
		updatePools();
		if( effects.size > 0 ) {
			for( int i = 0; i < effects.size; i++) {
				effects.get(i).update();
			}
		}
	}
	
	public void remove(BaboLightEffect effect) {
		environment.remove(effect.getLight());
	}
	
	/**
	 * Permet de n'afficher que les effets visibles a l'ecran
	 * Optimisation
	 */
	private boolean validEffect(BaboLightEffect effect) {
		if ( camera.frustum.pointInFrustum(effect.getPosition()) ) {
			return true;
		}
		return false;
	}
}
