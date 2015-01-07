package com.baboviolent.game.particle.effects;

import com.baboviolent.game.particle.batches.BaboParticleBatch;
import com.baboviolent.game.particle.influencers.RotationInfluencer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.BrownianAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.LineSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.utils.Array;


public class Smoke1Effect extends BaboParticleEffect {
	public static final String NAME = "smoke1";
	
	public Smoke1Effect(BaboParticleBatch batch) {
		super(batch);
		name = NAME;
		configure();
	}
	
	public void configure() {
		//Emitter
		RegularEmitter emitter = new RegularEmitter();
		emitter.setMinParticleCount(0);
		emitter.setMaxParticleCount(500);
		emitter.setContinuous(false);

		emitter.getEmission().setActive(true);
		emitter.getEmission().setLow(0);
		emitter.getEmission().setHigh(1500);
		emitter.getEmission().setScaling(new float[] {0.5f, 1});
		emitter.getEmission().setTimeline(new float[] {0, 1});
		
		emitter.getDuration().setActive(true);
		emitter.getDuration().setLow(200);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setLow(0);
		emitter.getLife().setHigh(500);
		emitter.getLife().setScaling(new float[] {1, 1});
		emitter.getLife().setTimeline(new float[] {0, 1});

		//Spawn
		LineSpawnShapeValue spawn = new LineSpawnShapeValue();
		spawn.setActive(true);
		spawn.setEdges(false);
		spawn.xOffsetValue.setActive(false);
		spawn.yOffsetValue.setActive(false);
		spawn.zOffsetValue.setActive(false);
		
		spawn.getSpawnWidth().setActive(true);
		spawn.getSpawnWidth().setLow(50);
		spawn.getSpawnWidth().setHigh(1000);
		spawn.getSpawnWidth().setScaling(new float[] {0, 1, 1});
		spawn.getSpawnWidth().setTimeline(new float[] {0, 0.5f, 1});
		
		spawn.getSpawnHeight().setActive(true);
		spawn.getSpawnHeight().setLow(0);
		spawn.getSpawnHeight().setHigh(5);
		spawn.getSpawnHeight().setScaling(new float[] {0, 1});
		spawn.getSpawnHeight().setTimeline(new float[] {0, 1});
		
		spawn.getSpawnDepth().setActive(true);
		spawn.getSpawnDepth().setLow(0);
		spawn.getSpawnDepth().setHigh(5);
		spawn.getSpawnDepth().setScaling(new float[] {0, 1});
		spawn.getSpawnDepth().setTimeline(new float[] {0, 1});

		SpawnInfluencer spawnSource = new SpawnInfluencer(spawn);

		//Scale
		ScaleInfluencer scaleInfluencer = new ScaleInfluencer();
		scaleInfluencer.value.setTimeline(new float[]{0, 1});
		scaleInfluencer.value.setScaling(new float[]{0, 1});
		scaleInfluencer.value.setLow(0);
		scaleInfluencer.value.setHigh(30);
		
		//TEST ROTATION
		RotationInfluencer rotationInfluencer = new RotationInfluencer();
		rotationInfluencer.value.setTimeline(new float[]{0, 1});
		rotationInfluencer.value.setScaling(new float[]{0, 1});
		rotationInfluencer.value.setLow(0);
		rotationInfluencer.value.setHigh(1);

		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
	
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {1, 0});
		
		colorInfluencer.colorValue.setColors(new float[] {0.56078434f, 0.69411767f, 0.8784314f, 0,0,0});
		colorInfluencer.colorValue.setTimeline(new float[] {0, 1});
		
		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(batch.getTexture()),
			spawnSource,
			scaleInfluencer,
			rotationInfluencer,
			colorInfluencer
			));
	}
}
