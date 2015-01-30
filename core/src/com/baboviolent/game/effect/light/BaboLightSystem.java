package com.baboviolent.game.effect.light;


import com.baboviolent.game.effect.light.effects.BaboLightEffect;
import com.baboviolent.game.effect.light.effects.Light1Effect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

@SuppressWarnings("deprecation")
public class BaboLightSystem {
	
	private ObjectMap<String, PoolLight> pools;
	private Environment environment;
	private Camera camera;
	private DirectionalShadowLight shadowLight;
	private Array<BaboLightEffect> effects;
	
	public BaboLightSystem(Camera camera, Environment environment) {
		this.environment = environment;
		this.camera = camera;
		initLight();
		initSystem();
	}
	
	private void initLight() {
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1));
		//environment.add(new DirectionalLight().set(new Color(0.8f,0.8f,0.8f,0.5f), new Vector3(0,-1,0).nor()));
		shadowLight = new DirectionalShadowLight(
				Gdx.graphics.getWidth()*2,
				Gdx.graphics.getHeight()*2, 
				100, 
				100, 1f, 60f);
		shadowLight.set(0.8f, 0.8f, 0.8f, new Vector3(-0.2f, -1, -0.2f).nor());
		environment.add(shadowLight);
		environment.shadowMap = shadowLight;
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
	
	public DirectionalShadowLight getShadowLight() {
		return shadowLight;
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
