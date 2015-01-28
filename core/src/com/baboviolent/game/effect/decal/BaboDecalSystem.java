package com.baboviolent.game.effect.decal;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.camera.BaboCamera;
import com.baboviolent.game.effect.light.PoolLight;
import com.baboviolent.game.effect.light.effects.BaboLightEffect;
import com.baboviolent.game.effect.light.effects.Light1Effect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class BaboDecalSystem {	
	private BaboCamera camera;
	private Cursor cursor;
	private Blood blood;
	
	public BaboDecalSystem(BaboCamera camera) {
		this.camera = camera;
		initSystem();
	}
	
	private void initSystem() {
		cursor = new Cursor(camera);
		blood = new Blood();
	}
	
	public void render(DecalBatch decalBatch) {
		cursor.render(decalBatch);
		blood.render(decalBatch);
	}
	
	public void cursorHit() {
		cursor.hit();
	}
	
	public void generateBlood(Vector3 position, float damage) {
		blood.generateBlood(position, damage);
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
