package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.baboviolent.game.effect.particle.influencers.RotationInfluencer;
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
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.LineSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Array;


public class Bullet1Effect extends BaboParticleEffect {
	public static final String NAME = "bullet1";
	
	private Matrix4 tmpM = new Matrix4();
	private Quaternion tmpQ = new Quaternion();
	
	public Bullet1Effect(BaboParticleBatch batch) {
		super(batch);
		name = NAME;
		textureFaceDirection = true;
		configure();
	}
	
	public Bullet1Effect(Bullet1Effect effect) {
		super(effect);
	}
	
	@Override
	public Bullet1Effect copy() {
		return new Bullet1Effect(this);
	}
	
	@Override
	public void init() {
		ParticleController c = getControllers().get(0);
		DynamicsInfluencer d = c.findInfluencer(DynamicsInfluencer.class);
		PolarAcceleration pa = (PolarAcceleration) d.velocities.get(0);
		
		c.getTransform(tmpM);
		tmpM.getRotation(tmpQ);
		float angle = getAngleFromQuaternion(tmpQ);
		
		pa.thetaValue.setHigh(angle);
		
		super.init();
	}
	
	public void configure() {
		//Emitter
		RegularEmitter emitter = new RegularEmitter();
		emitter.setMinParticleCount(1);
		emitter.setMaxParticleCount(1);
		emitter.setContinuous(false);

		emitter.getEmission().setActive(true);
		emitter.getEmission().setHigh(1);
		
		emitter.getDuration().setActive(true);
		emitter.getDuration().setLow(100);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setLow(0);
		emitter.getLife().setHigh(2000);
		emitter.getLife().setScaling(new float[] {1, 1});
		emitter.getLife().setTimeline(new float[] {0, 1});

		//Spawn
		PointSpawnShapeValue spawn = new PointSpawnShapeValue();
		spawn.setActive(true);
		spawn.setEdges(false);
		spawn.xOffsetValue.setActive(false);
		spawn.yOffsetValue.setActive(false);
		spawn.zOffsetValue.setActive(false);
		SpawnInfluencer spawnSource = new SpawnInfluencer(spawn);

		//Scale
		ScaleInfluencer scaleInfluencer = new ScaleInfluencer();
		scaleInfluencer.value.setTimeline(new float[]{0, 1});
		scaleInfluencer.value.setScaling(new float[]{0, 1});
		scaleInfluencer.value.setHigh(30);
		scaleInfluencer.value.setLow(0);
		
		// Rotation qui sera mis a jour a chaque tir en fonction de l'angle
		RotationInfluencer rotationInfluencer = new RotationInfluencer();
		rotationInfluencer.value.setHigh(0);
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
	
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {1, 1});

		//colorInfluencer.colorValue.setColors(new float[] {0.7f,0.7f,0.7f});
		colorInfluencer.colorValue.setColors(new float[] {1,1,1});
		colorInfluencer.colorValue.setTimeline(new float[] {0});
		
		//Dynamics
		DynamicsInfluencer dynamicsInfluencer = new DynamicsInfluencer();
		
		PolarAcceleration modifier = new PolarAcceleration();
		modifier.strengthValue.setTimeline(new float[]{0.01f,0.02f, 1});
		modifier.strengthValue.setScaling(new float[]{1,0,0});
		modifier.strengthValue.setLow(0);
		modifier.strengthValue.setHigh(10000);
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
			colorInfluencer,
			rotationInfluencer,
			dynamicsInfluencer
			));
	}
}
