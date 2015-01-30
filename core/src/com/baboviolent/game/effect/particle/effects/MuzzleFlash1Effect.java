package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.baboviolent.game.effect.particle.influencers.PositionInfluencer;
import com.baboviolent.game.effect.particle.influencers.TextureFaceDirectionInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;

/**
 * Trainee de fumme lors du tir
 */
public class MuzzleFlash1Effect extends BaboParticleEffect {
	public static final String NAME = "muzzleflash1";
	
	private Matrix4 tmpM = new Matrix4();
	private Quaternion tmpQ = new Quaternion();
	
	public MuzzleFlash1Effect(BaboParticleBatch batch) {
		super(batch);
		name = NAME;
		configure();
	}
	
	public MuzzleFlash1Effect(MuzzleFlash1Effect effect) {
		super(effect);
	}
	
	@Override
	public MuzzleFlash1Effect copy() {
		return new MuzzleFlash1Effect(this);
	}
	
	/**
	 * Avance legerement la flemme pour la voir en entiere
	 */
	@Override
	public void init() {
		ParticleController c = getControllers().get(0);
		PositionInfluencer p = c.findInfluencer(PositionInfluencer.class);
		
		c.getTransform(tmpM);
		tmpM.getRotation(tmpQ);
		float angle = getAngleFromQuaternion(tmpQ);
		p.thetaValue = angle;
		
		super.init();
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
		emitter.getDuration().setLow(10);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setHigh(100);

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
		scaleInfluencer.value.setTimeline(new float[]{0,0.5f,1});
		scaleInfluencer.value.setScaling(new float[]{0,1, 1});
		scaleInfluencer.value.setHigh(1);
		scaleInfluencer.value.setLow(0.02f);
		
		// Position
		PositionInfluencer positionInfluencer = new PositionInfluencer();
		positionInfluencer.strengthValue.setTimeline(new float[]{1});
		positionInfluencer.strengthValue.setScaling(new float[]{1});
		positionInfluencer.strengthValue.setHigh(0.3f);
		positionInfluencer.phiValue = 90;
		positionInfluencer.thetaValue = 0;
		
		// FaceDirection
		TextureFaceDirectionInfluencer textureFaceInfluencer = new TextureFaceDirectionInfluencer();
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 0.5f, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {1,1,0});

		colorInfluencer.colorValue.setColors(new float[] {1,1,1});
		colorInfluencer.colorValue.setTimeline(new float[] {0});
		
		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(batch.getTexture()),
			spawnSource,
			scaleInfluencer,
			colorInfluencer,
			textureFaceInfluencer,
			positionInfluencer
			));
	}
}
