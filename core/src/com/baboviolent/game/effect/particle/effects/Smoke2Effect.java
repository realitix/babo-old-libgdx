package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.PolarAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.Rotational2D;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Fumme apres l'impacte
 * Et rebond si impact detecte
 */
public class Smoke2Effect extends BaboParticleEffect {
	public static final String NAME = "smoke2";
	
	private Matrix4 tmpM = new Matrix4();
	private Vector3 normalRay;
	
	public Smoke2Effect(BaboParticleBatch batch) {
		super(batch);
		name = NAME;
		configure();
	}
	
	public Smoke2Effect(Smoke2Effect effect) {
		super(effect);
	}
	
	@Override
	public Smoke2Effect copy() {
		return new Smoke2Effect(this);
	}
	
	@Override
	public void setNormalRay(Vector3 normalRay) {
		this.normalRay = normalRay;
	}
	
	/*
	 * S'il n'y a pas de normal, on ne garde que le premier controleur
	 * Sinon on verifie la presence du second controleur et on dirige la fumee dans le bon sens
	 * @see com.badlogic.gdx.graphics.g3d.particles.ParticleEffect#init()
	 */
	@Override
	public void init() {		
		if( normalRay == null ) {
			if( getControllers().size > 1 ) {
				getControllers().removeIndex(1);
			}
		}
		else {
			if( getControllers().size < 2 ) {
				getControllers().add(configure2());
			}
			
			ParticleController c = findController("smokeNormal");
			getControllers().get(0).getTransform(tmpM);
			c.setTransform(tmpM);
			DynamicsInfluencer d = c.findInfluencer(DynamicsInfluencer.class);
			
			// On regle le troisieme en fonction de la normal
			PolarAcceleration pa = (PolarAcceleration) d.velocities.get(2);
			float angle = getAngleFromNormalRay(normalRay);
			angle += MathUtils.random(-30, 30);
			pa.thetaValue.setHigh(angle);
		}
		
		super.init();
	}
	
	
	private float getAngleFromNormalRay(Vector3 normalRay) {
		Vector2 v2 = new Vector2(normalRay.x, normalRay.z);
		return v2.angle();
	}
	
	private ParticleController configure2() {
		//Emitter
		RegularEmitter emitter = new RegularEmitter();
		emitter.setMinParticleCount(1);
		emitter.setMaxParticleCount(2);
		emitter.setContinuous(false);

		emitter.getEmission().setActive(true);
		emitter.getEmission().setHigh(10);
		
		emitter.getDuration().setActive(true);
		emitter.getDuration().setLow(300);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setHigh(600);

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
		scaleInfluencer.value.setLow(0.6f);
		scaleInfluencer.value.setHigh(0.6f);
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
	
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {1, 0});
		
		colorInfluencer.colorValue.setColors(new float[] {1,1,1,1,1,1});
		colorInfluencer.colorValue.setTimeline(new float[] {0, 1});
		
		//Dynamics
		DynamicsInfluencer dynamicsInfluencer = new DynamicsInfluencer();
		
		Rotational2D modifier1 = new Rotational2D();
		modifier1.strengthValue.setTimeline(new float[]{0});
		modifier1.strengthValue.setScaling(new float[]{1});
		modifier1.strengthValue.setHigh(150);
		
		PolarAcceleration modifier2 = new PolarAcceleration();
		modifier2.strengthValue.setTimeline(new float[]{0});
		modifier2.strengthValue.setScaling(new float[]{1});
		modifier2.strengthValue.setHigh(0.3f);
		
		PolarAcceleration modifier3 = new PolarAcceleration();
		modifier3.strengthValue.setTimeline(new float[]{0, 1});
		modifier3.strengthValue.setScaling(new float[]{1, 0});
		modifier3.strengthValue.setHigh(18);
		modifier3.strengthValue.setLow(2);
		modifier3.phiValue.setTimeline(new float[]{0});
		modifier3.phiValue.setScaling(new float[]{1});
		modifier3.phiValue.setHigh(90);
		modifier3.thetaValue.setTimeline(new float[]{0});
		modifier3.thetaValue.setScaling(new float[]{1});
		
		dynamicsInfluencer.velocities.add(modifier1);
		dynamicsInfluencer.velocities.add(modifier2);
		dynamicsInfluencer.velocities.add(modifier3);
		
		return new ParticleController("smokeNormal", emitter, new BillboardRenderer(batch),
				new RegionInfluencer.Single(batch.getTexture()),
				spawnSource,
				scaleInfluencer,
				colorInfluencer,
				dynamicsInfluencer
				);
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
		emitter.getLife().setHigh(1500);
		
		emitter.getDelay().setActive(true);
		emitter.getDelay().setLow(100);

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
		scaleInfluencer.value.setLow(0.3f);
		scaleInfluencer.value.setHigh(2);
		
		//Color
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
	
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {1, 0});
		
		colorInfluencer.colorValue.setColors(new float[] {1,1,1,1,1,1});
		colorInfluencer.colorValue.setTimeline(new float[] {0, 1});
		
		//Dynamics
		DynamicsInfluencer dynamicsInfluencer = new DynamicsInfluencer();
		
		Rotational2D modifier1 = new Rotational2D();
		modifier1.strengthValue.setTimeline(new float[]{0});
		modifier1.strengthValue.setScaling(new float[]{1});
		modifier1.strengthValue.setHigh(150);
		
		PolarAcceleration modifier2 = new PolarAcceleration();
		modifier2.strengthValue.setTimeline(new float[]{0});
		modifier2.strengthValue.setScaling(new float[]{1});
		modifier2.strengthValue.setHigh(0.3f);
		
		dynamicsInfluencer.velocities.add(modifier1);
		dynamicsInfluencer.velocities.add(modifier2);
		
		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			new RegionInfluencer.Single(batch.getTexture()),
			spawnSource,
			scaleInfluencer,
			colorInfluencer,
			dynamicsInfluencer
			));
	}
}
