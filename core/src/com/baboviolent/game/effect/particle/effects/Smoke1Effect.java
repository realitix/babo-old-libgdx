package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.baboviolent.game.effect.particle.influencers.PositionInfluencer;
import com.baboviolent.game.effect.particle.influencers.ScaleHeightInfluencer;
import com.baboviolent.game.effect.particle.influencers.ScaleWidthInfluencer;
import com.baboviolent.game.effect.particle.influencers.TextureFaceDirectionInfluencer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
public class Smoke1Effect extends BaboParticleEffect {
	public static final String NAME = "smoke1";
	
	private Matrix4 tmpM = new Matrix4();
	private Quaternion tmpQ = new Quaternion();
	
	public Smoke1Effect(BaboParticleBatch batch, TextureAtlas atlas) {
		super(batch, atlas);
		name = NAME;
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
		emitter.getDuration().setLow(100);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setHigh(800);

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
		scaleWidthInfluencer.value.setTimeline(new float[]{0, 0.3f, 1});
		scaleWidthInfluencer.value.setScaling(new float[]{0, 0.5f, 1});
		scaleWidthInfluencer.value.setLow(0);
		scaleWidthInfluencer.value.setHigh(0.5f);
		
		//Scale Height
		ScaleHeightInfluencer scaleHeightInfluencer = new ScaleHeightInfluencer();
		scaleHeightInfluencer.value.setTimeline(new float[]{0, 0.3f, 1});
		scaleHeightInfluencer.value.setScaling(new float[]{0, 1, 1});
		scaleHeightInfluencer.value.setLow(0.1f);
		scaleHeightInfluencer.value.setHigh(10);
		
		// Position
		PositionInfluencer positionInfluencer = new PositionInfluencer();
		positionInfluencer.strengthValue.setTimeline(new float[]{0, 0.3f, 1});
		positionInfluencer.strengthValue.setScaling(new float[]{0, 1, 1});
		positionInfluencer.strengthValue.setLow(0);
		positionInfluencer.strengthValue.setHigh(5);
		positionInfluencer.phiValue = 90;
		positionInfluencer.thetaValue = 0;
		
		// FaceDirection
		TextureFaceDirectionInfluencer textureFaceInfluencer = new TextureFaceDirectionInfluencer();
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
	
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {0.5f, 0});

		colorInfluencer.colorValue.setColors(new float[] {0.9f,0.9f,0.9f,0.7f,0.7f,0.7f});
		colorInfluencer.colorValue.setTimeline(new float[] {0, 1});

		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(atlas.findRegion("smokeTrail")),
			spawnSource,
			scaleInfluencer,
			scaleWidthInfluencer,
			scaleHeightInfluencer,
			colorInfluencer,
			textureFaceInfluencer,
			positionInfluencer
			));
	}
	
	@Override
	public void setWidth(float width) {
		getControllers()
		.get(0)
		.findInfluencer(ScaleHeightInfluencer.class)
		.value.setHigh(width);
		
		getControllers()
		.get(0)
		.findInfluencer(PositionInfluencer.class)
		.strengthValue.setHigh(width/2);
	}
}
