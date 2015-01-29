package com.baboviolent.game.effect.particle.effects;

import com.baboviolent.game.effect.particle.batches.BaboParticleBatch;
import com.baboviolent.game.effect.particle.influencers.PositionInfluencer;
import com.baboviolent.game.effect.particle.influencers.RandomBloodColorInfluencer;
import com.baboviolent.game.effect.particle.influencers.RotationInfluencer;
import com.baboviolent.game.effect.particle.influencers.ScaleHeightInfluencer;
import com.baboviolent.game.effect.particle.influencers.ScaleWidthInfluencer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.TangentialAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer.AspectTextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.BrownianAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.PolarAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.Rotational2D;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.LineSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Array;


public class Blood1Effect extends BaboParticleEffect {
	public static final String NAME = "blood1";
	
	private TextureAtlas atlas;
	
	public Blood1Effect(BaboParticleBatch batch, TextureAtlas atlas) {
		super(batch);
		name = NAME;
		this.atlas = atlas;
		configure();
	}
	
	public Blood1Effect(Blood1Effect effect) {
		super(effect);
		this.atlas = effect.getAtlas();
	}
	
	@Override
	public Blood1Effect copy() {
		return new Blood1Effect(this);
	}
	
	public void configure() {
		//Emitter
		RegularEmitter emitter = new RegularEmitter();
		emitter.setMinParticleCount(10);
		emitter.setMaxParticleCount(200);
		emitter.setContinuous(false);

		emitter.getEmission().setActive(true);
		emitter.getEmission().setHigh(100);
		
		emitter.getDuration().setActive(true);
		emitter.getDuration().setLow(600);
		
		emitter.getLife().setActive(true);
		emitter.getLife().setHigh(1000);
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
		scaleInfluencer.value.setTimeline(new float[]{0, 1});
		scaleInfluencer.value.setScaling(new float[]{0,1});
		scaleInfluencer.value.setLow(0);
		scaleInfluencer.value.setHigh(1);

		//Color
		RandomBloodColorInfluencer colorInfluencer = new RandomBloodColorInfluencer();
		colorInfluencer.alphaValue.setActive(true);
		colorInfluencer.alphaValue.setLow(0);
		colorInfluencer.alphaValue.setHigh(1);
		colorInfluencer.alphaValue.setTimeline(new float[] {0, 1});
		colorInfluencer.alphaValue.setScaling(new float[] {1, 0});
		
		// Region
		Array<TextureAtlas.AtlasRegion> regions = atlas.getRegions();
		RegionInfluencer.Random regionInfluencer = new RegionInfluencer.Random();
		regionInfluencer.regions = new Array<AspectTextureRegion>( false, regions.size, AspectTextureRegion.class);
		for( int i = 0; i < regions.size; i++ ) {
			regionInfluencer.add(regions.get(i));
		}
		
		//Dynamics
		// Il faut faire brownian plus polar
		DynamicsInfluencer dynamicsInfluencer = new DynamicsInfluencer();
		
		BrownianAcceleration modifier1 = new BrownianAcceleration();
		modifier1.strengthValue.setTimeline(new float[]{0});
		modifier1.strengthValue.setScaling(new float[]{1});
		modifier1.strengthValue.setHigh(5000);
		
		TangentialAcceleration modifier2 = new TangentialAcceleration();
		modifier2.strengthValue.setTimeline(new float[]{0});
		modifier2.strengthValue.setScaling(new float[]{1});
		modifier2.strengthValue.setHigh(10);
		
		dynamicsInfluencer.velocities.add(modifier1);
		//dynamicsInfluencer.velocities.add(modifier2);
		
		getControllers().add(new ParticleController(name, emitter, new BillboardRenderer(batch),
			regionInfluencer,
			spawnSource,
			scaleInfluencer,
			colorInfluencer,
			dynamicsInfluencer
			));
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
	}
}
