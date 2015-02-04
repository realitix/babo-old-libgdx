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
		//environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1));
		//environment.add(new DirectionalLight().set(new Color(0.8f,0.8f,0.8f,0.5f), new Vector3(0,-1,0).nor()));
		
		/**
		 * Les deux premiers arguments sont la resolution de l'ombre
		 * Plus c'est faible moins ca consomme en memoire
		 * Les deux arguments suivants sont la taille de l'ombre sur l'image
		 * 37 est la valeur min.
		 * Modifie les 3 et 4 eme argument impacte les deux premiers
		 * Plus la taille de la camera est grande et plus la taille de la
		 * shadow map doit etre grande.
		 * Il faut donc mettre ces deux valeurs aux plus petites possibles
		 * Les deux dernieres sont la distance par rapport a la camera
		 */
		shadowLight = new DirectionalShadowLight(
				512, 512, 37, 37, 1f, 30f);
		shadowLight.set(0.8f, 0.8f, 0.8f, new Vector3(-0.5f, -1, -0.5f).nor());
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
	
	public void update() {
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
