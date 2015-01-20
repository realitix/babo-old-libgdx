package com.baboviolent.game.effect.particle.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.influencers.Influencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer.AspectTextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Non utilise
 * Peut etre utile plus tard
 *
 */
public class PositionInfluencer extends Influencer {
	
	public ScaledNumericValue valueX;
	private FloatChannel positionChannel, interpolationChannel, lifeChannel;
	private float initX;
	
	public PositionInfluencer() {
		valueX = new ScaledNumericValue();
		valueX.setHigh(1);
	}
	
	public PositionInfluencer(PositionInfluencer influencer) {
		this();
		set(influencer);
	}
	
	private void set (PositionInfluencer influencer) {
		valueX.load(influencer.valueX);
	}
	
	@Override
	public void allocateChannels() {
		positionChannel = controller.particles.addChannel(ParticleChannels.Position);
		ParticleChannels.Interpolation.id = controller.particleChannels.newId();
		interpolationChannel = controller.particles.addChannel(ParticleChannels.Interpolation);
		lifeChannel = controller.particles.addChannel(ParticleChannels.Life);
	}
	
	@Override
	public void activateParticles (int startIndex, int count) {		
		for(int i = startIndex*positionChannel.strideSize, a = startIndex*interpolationChannel.strideSize, c = i +count*positionChannel.strideSize; 
			i < c;  i += positionChannel.strideSize, a+=interpolationChannel.strideSize){
			float start = valueX.newLowValue();
			float diff = valueX.newHighValue();
			interpolationChannel.data[a +ParticleChannels.InterpolationStartOffset] = start;
			interpolationChannel.data[a +ParticleChannels.InterpolationDiffOffset] = diff;
		}
	}
	
	@Override
	public void update() {
		for(int i = 0, a = 0, l = ParticleChannels.LifePercentOffset,
			c = controller.particles.size*positionChannel.strideSize; 
			i < c; 
			i += positionChannel.strideSize, a += interpolationChannel.strideSize,
			l += lifeChannel.strideSize) {
			
			float val = interpolationChannel.data[a +ParticleChannels.InterpolationStartOffset] + 
					interpolationChannel.data[a +ParticleChannels.InterpolationDiffOffset] * valueX.getScale(lifeChannel.data[l]);
			
			if( initX == 0 ) {
				initX = positionChannel.data[i+ParticleChannels.XOffset];
			}
			positionChannel.data[i+ParticleChannels.XOffset] = initX + val;
		}
	}
	
	@Override
	public void write (Json json) {
		json.writeValue("valueX", valueX);
	}
	
	@Override
	public void read (Json json, JsonValue jsonData) {
		valueX = json.readValue("valueX", ScaledNumericValue.class, jsonData);
	}

	@Override
	public ParticleControllerComponent copy () {
		return new PositionInfluencer(this);
	}

}
