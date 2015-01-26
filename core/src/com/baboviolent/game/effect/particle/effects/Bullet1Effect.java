package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.baboviolent.game.effect.particle.influencers.PositionInfluencer;
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
	private float life = 400;
	private float initWidth = 1000;
	
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
		emitter.getEmission().setHigh(1);
		
		emitter.getDuration().setActive(true);
		emitter.getDuration().setLow(50);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setHigh(life);
		emitter.getLife().setScaling(new float[] {1});
		emitter.getLife().setTimeline(new float[] {0});

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
		scaleWidthInfluencer.value.setLow(10);
		scaleWidthInfluencer.value.setHigh(10);
		
		//Scale Height
		ScaleHeightInfluencer scaleHeightInfluencer = new ScaleHeightInfluencer();
		scaleHeightInfluencer.value.setTimeline(new float[]{0, 0.3f, 1});
		scaleHeightInfluencer.value.setScaling(new float[]{0, 1, 1});
		scaleHeightInfluencer.value.setLow(10);
		scaleHeightInfluencer.value.setHigh(50);
		
		// Rotation qui sera mis a jour a chaque tir en fonction de l'angle
		RotationInfluencer rotationInfluencer = new RotationInfluencer();
		rotationInfluencer.value.setHigh(0);
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {1, 0});
		colorInfluencer.colorValue.setColors(new float[] {0.9f,0.5f,0.5f});
		colorInfluencer.colorValue.setTimeline(new float[] {0});

		
		// Position
		PositionInfluencer positionInfluencer = new PositionInfluencer();
		positionInfluencer.strengthValue.setTimeline(new float[]{0,1});
		positionInfluencer.strengthValue.setScaling(new float[]{0,1});
		positionInfluencer.strengthValue.setHigh(0);
		positionInfluencer.strengthValue.setHigh(initWidth);
		positionInfluencer.phiValue = 90;
		positionInfluencer.thetaValue = 0;
		
		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(batch.getTexture()),
			spawnSource,
			scaleInfluencer,
			scaleWidthInfluencer,
			scaleHeightInfluencer,
			colorInfluencer,
			rotationInfluencer,
			positionInfluencer
			));
	}
	
	@Override
	public void setWidth(float width) {		
		getControllers()
		.get(0)
		.findInfluencer(PositionInfluencer.class)
		.strengthValue.setHigh(width);
		
		((RegularEmitter)getControllers()
		.get(0)
		.emitter)
		.getLife()
		.setHigh(width*life/initWidth);
	}
}
