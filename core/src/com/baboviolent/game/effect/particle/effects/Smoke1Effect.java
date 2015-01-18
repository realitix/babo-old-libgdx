package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.baboviolent.game.effect.particle.influencers.RotationInfluencer;
import com.baboviolent.game.effect.particle.influencers.ScaleHeightInfluencer;
import com.baboviolent.game.effect.particle.influencers.ScaleWidthInfluencer;
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
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.PolarAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.Rotational2D;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.LineSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;


public class Smoke1Effect extends BaboParticleEffect {
	public static final String NAME = "smoke1";
	
	public Smoke1Effect(BaboParticleBatch batch) {
		super(batch);
		name = NAME;
		textureFaceDirection = true;
		configure();
	}
	
	public Smoke1Effect(Smoke1Effect effect) {
		super(effect);
	}
	
	@Override
	public Smoke1Effect copy() {
		return new Smoke1Effect(this);
	}
	
	@Override
	public void init() {
		DynamicsInfluencer d = getControllers().get(0).findInfluencer(DynamicsInfluencer.class);
		PolarAcceleration pa = (PolarAcceleration) d.velocities.get(0);
		pa.thetaValue.setHigh(getAngleFromNormalRay(normalRay));
		
		super.init();
	}
	
	// @TODO
	// test en modifiant la taille de la texture
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
		emitter.getLife().setHigh(2500);

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
		scaleInfluencer.value.setHigh(1);
		
		//Scale Width
		ScaleWidthInfluencer scaleWidthInfluencer = new ScaleWidthInfluencer();
		scaleWidthInfluencer.value.setTimeline(new float[]{0, 1});
		scaleWidthInfluencer.value.setScaling(new float[]{0, 1});
		scaleWidthInfluencer.value.setLow(10);
		scaleWidthInfluencer.value.setHigh(100);
		
		//Scale Height
		ScaleHeightInfluencer scaleHeightInfluencer = new ScaleHeightInfluencer();
		scaleHeightInfluencer.value.setTimeline(new float[]{0, 1});
		scaleHeightInfluencer.value.setScaling(new float[]{0, 1});
		scaleHeightInfluencer.value.setLow(10);
		scaleHeightInfluencer.value.setHigh(1000);
		
		// Rotation qui sera mis a jour a chaque tir en fonction de l'angle
		RotationInfluencer rotationInfluencer = new RotationInfluencer();
		rotationInfluencer.value.setHigh(0);
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
	
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {0.9f, 0});

		colorInfluencer.colorValue.setColors(new float[] {0.9f,0.9f,0.9f,0.7f,0.7f,0.7f});
		colorInfluencer.colorValue.setTimeline(new float[] {0, 1});
		
		//Dynamics
		DynamicsInfluencer dynamicsInfluencer = new DynamicsInfluencer();
		
		PolarAcceleration modifier = new PolarAcceleration();
		modifier.strengthValue.setTimeline(new float[]{0, 1});
		modifier.strengthValue.setScaling(new float[]{1, 0});
		modifier.strengthValue.setHigh(1800);
		modifier.strengthValue.setLow(200);
		modifier.phiValue.setTimeline(new float[]{0});
		modifier.phiValue.setScaling(new float[]{1});
		modifier.phiValue.setHigh(90);
		modifier.thetaValue.setTimeline(new float[]{0});
		modifier.thetaValue.setScaling(new float[]{1});
		dynamicsInfluencer.velocities.add(modifier);
		
		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(batch.getTexture()),
			spawnSource,
			scaleInfluencer,
			scaleWidthInfluencer,
			scaleHeightInfluencer,
			colorInfluencer,
			rotationInfluencer,
			dynamicsInfluencer
			));
	}
	
	public void configure2() {
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
		emitter.getDuration().setLow(150);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setLow(0);
		emitter.getLife().setHigh(600);
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
		spawn.getSpawnWidth().setScaling(new float[] {0, 1});
		spawn.getSpawnWidth().setTimeline(new float[] {0, 1});
		
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
		scaleInfluencer.value.setHigh(20);
		
		// Rotation qui sera mis a jour a chaque tir en fonction de l'angle
		RotationInfluencer rotationInfluencer = new RotationInfluencer();
		rotationInfluencer.value.setHigh(0);
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
	
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {0.9f, 0});

		colorInfluencer.colorValue.setColors(new float[] {0.9f,0.9f,0.9f,0.7f,0.7f,0.7f});
		colorInfluencer.colorValue.setTimeline(new float[] {0, 1});
		
		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(batch.getTexture()),
			spawnSource,
			scaleInfluencer,
			colorInfluencer,
			rotationInfluencer
			));
	}
	
	/*@Override
	public void setWidth(float width) {
		((LineSpawnShapeValue) (
				getControllers()
				.get(0)
				.findInfluencer(SpawnInfluencer.class)
				.spawnShapeValue))
				.getSpawnWidth()
				.setHigh(width);
	}*/
}
