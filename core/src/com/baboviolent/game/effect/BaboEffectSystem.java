package com.baboviolent.game.effect;

import com.baboviolent.game.camera.BaboCamera;
import com.baboviolent.game.effect.decal.BaboDecalSystem;
import com.baboviolent.game.effect.group.Blood1;
import com.baboviolent.game.effect.group.CursorHit;
import com.baboviolent.game.effect.group.GroupEffect;
import com.baboviolent.game.effect.group.Shoot1;
import com.baboviolent.game.effect.light.BaboLightSystem;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.sound.BaboSoundSystem;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.ObjectMap;

public class BaboEffectSystem {
	private BaboParticleSystem particleSystem;
	private BaboLightSystem lightSystem;
	private BaboDecalSystem decalSystem;
	private BaboSoundSystem soundSystem;
	private ObjectMap<String, GroupEffect> groups;
	
	public BaboEffectSystem(BaboCamera camera, Environment environment) {
		groups = new ObjectMap<String, GroupEffect>();
		particleSystem = new BaboParticleSystem(camera);
		decalSystem = new BaboDecalSystem(camera);
		lightSystem = new BaboLightSystem(camera, environment);
		soundSystem = new BaboSoundSystem();
		initGroups();
	}
	
	private void initGroups() {
		groups.put(Shoot1.NAME, new Shoot1(this));
		groups.put(CursorHit.NAME, new CursorHit(this));
		groups.put(Blood1.NAME, new Blood1(this));
	}
	
	public GroupEffect get(String name) {
		return groups.get(name);
	}
	
	public void update() {
		particleSystem.update();
		lightSystem.update();
		decalSystem.update();
	}
	
	public void render(ModelBatch modelBatch, Environment environment) {
		particleSystem.render(modelBatch, environment);
	}
	
	public void renderDecals(DecalBatch decalBatch) {
		decalSystem.render(decalBatch);
	}
	
	public void renderCursor(DecalBatch decalBatch) {
		decalSystem.renderCursor(decalBatch);
	}
	
	public BaboParticleSystem getParticleSystem() {
		return particleSystem;
	}
	
	public BaboLightSystem getLightSystem() {
		return lightSystem;
	}
	
	public BaboDecalSystem getDecalSystem() {
		return decalSystem;
	}
	
	public BaboSoundSystem getSoundSystem() {
		return soundSystem;
	}
}
