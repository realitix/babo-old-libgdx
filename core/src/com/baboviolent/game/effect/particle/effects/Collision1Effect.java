package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;

/**
 * Trainee de fumme lors du tir
 */
public class Collision1Effect extends BaboParticleEffect {
	public static final String NAME = "collision1";
	
	public Collision1Effect(BaboParticleBatch batch, TextureAtlas atlas) {
		super(batch, atlas);
		name = NAME;
		configure();
	}
	
	public Collision1Effect(Collision1Effect effect) {
		super(effect);
	}
	
	@Override
	public Collision1Effect copy() {
		return new Collision1Effect(this);
	}
	
	public void configure() {
		//Emitter
		RegularEmitter emitter = new RegularEmitter();
		emitter.setMinParticleCount(1);
		emitter.setMaxParticleCount(1);
		emitter.setContinuous(false);

		emitter.getEmission().setActive(true);
		emitter.getEmission().setLow(1);
		
		emitter.getDuration().setActive(true);
		emitter.getDuration().setLow(100);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setHigh(200);

		//Spawn
		PointSpawnShapeValue spawn = new PointSpawnShapeValue();
		spawn.setActive(true);
		spawn.setEdges(false);
		spawn.xOffsetValue.setActive(false);
		spawn.yOffsetValue.setActive(false);
		spawn.zOffsetValue.setActive(false);
		SpawnInfluencer spawnSource = new SpawnInfluencer(spawn);

		// Scale
		ScaleInfluencer scaleInfluencer = new ScaleInfluencer();
		scaleInfluencer.value.setTimeline(new float[]{0});
		scaleInfluencer.value.setScaling(new float[]{1});
		scaleInfluencer.value.setHigh(0.32f);
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0,1});
		colorInfluencer.alphaValue.setScaling(new float[] {1,0});

		colorInfluencer.colorValue.setColors(new float[] {1,1,1});
		colorInfluencer.colorValue.setTimeline(new float[] {0});
		
		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(atlas.findRegion("shotGlow")),
			spawnSource,
			scaleInfluencer,
			colorInfluencer
			));
	}
}
