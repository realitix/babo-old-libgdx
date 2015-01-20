package com.baboviolent.game.effect;

import com.baboviolent.game.effect.group.GroupEffect;
import com.baboviolent.game.effect.group.Shoot1;
import com.baboviolent.game.effect.light.BaboLightSystem;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.ObjectMap;

public class BaboEffectSystem {
	private BaboParticleSystem particleSystem;
	private BaboLightSystem lightSystem;
	private ObjectMap<String, GroupEffect> groups;
	
	public BaboEffectSystem(Camera camera, Environment environment) {
		groups = new ObjectMap<String, GroupEffect>();
		particleSystem = new BaboParticleSystem(camera);
		lightSystem = new BaboLightSystem(camera, environment);
		initGroups();
	}
	
	private void initGroups() {
		groups.put(Shoot1.NAME, new Shoot1(this));
	}
	
	public GroupEffect get(String name) {
		return groups.get(name);
	}
	
	public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment) {
		particleSystem.render(modelBatch, environment);
		lightSystem.render();
	}
	
	public BaboParticleSystem getParticleSystem() {
		return particleSystem;
	}
	
	public BaboLightSystem getLightSystem() {
		return lightSystem;
	}
}
