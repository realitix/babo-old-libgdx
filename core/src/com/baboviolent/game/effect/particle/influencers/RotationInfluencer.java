package com.baboviolent.game.effect.particle.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.ChannelDescriptor;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.influencers.Influencer;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;


public class RotationInfluencer  extends Influencer {
	
	public ScaledNumericValue value;
	private FloatChannel rotationChannel, interpolationChannel, lifeChannel;
	
	public RotationInfluencer() {
		value = new ScaledNumericValue();
		value.setHigh(1);
	}
	
	public RotationInfluencer(RotationInfluencer influencer) {
		this();
		set(influencer);
	}
	
	private void set (RotationInfluencer influencer) {
		value.load(influencer.value);
	}
	
	@Override
	public void allocateChannels() {
		rotationChannel = controller.particles.addChannel(ParticleChannels.Rotation2D);
		ParticleChannels.Interpolation.id = controller.particleChannels.newId();
		interpolationChannel = controller.particles.addChannel(ParticleChannels.Interpolation);
		lifeChannel = controller.particles.addChannel(ParticleChannels.Life);
	}
	
	@Override
	public void activateParticles (int startIndex, int count) {
		for(int i=startIndex*rotationChannel.strideSize, c = i +count*rotationChannel.strideSize; i< c;  i+= rotationChannel.strideSize){
			rotationChannel.data[i+ParticleChannels.CosineOffset] = 1;
			rotationChannel.data[i+ParticleChannels.SineOffset] = 0;
		}
		
		for(int i = startIndex*rotationChannel.strideSize, a = startIndex*interpolationChannel.strideSize, c = i +count*rotationChannel.strideSize; 
			i < c;  i += rotationChannel.strideSize, a+=interpolationChannel.strideSize){
			float start = value.newLowValue();
			float diff = value.newHighValue();
			interpolationChannel.data[a +ParticleChannels.InterpolationStartOffset] = start;
			interpolationChannel.data[a +ParticleChannels.InterpolationDiffOffset] = diff;
		}
	}
	
	@Override
	public void update() {
		for(int i = 0, a = 0, l = ParticleChannels.LifePercentOffset,
			offset = 0,
			c = i + controller.particles.size*rotationChannel.strideSize; 
			i < c; 
			i += rotationChannel.strideSize, a += interpolationChannel.strideSize,
			l += lifeChannel.strideSize, offset += rotationChannel.strideSize) {
			
			float rotation = interpolationChannel.data[a +ParticleChannels.InterpolationStartOffset] + 
															interpolationChannel.data[a +ParticleChannels.InterpolationDiffOffset] * value.getScale(lifeChannel.data[l]);
			float cosBeta = MathUtils.cosDeg(rotation), sinBeta = MathUtils.sinDeg(rotation);
			rotationChannel.data[offset + ParticleChannels.CosineOffset] = cosBeta;
			rotationChannel.data[offset + ParticleChannels.SineOffset] = sinBeta;
		}
	}
	
	@Override
	public void write (Json json) {
		json.writeValue("value", value);
	}
	
	@Override
	public void read (Json json, JsonValue jsonData) {
		value = json.readValue("value", ScaledNumericValue.class, jsonData);
	}

	@Override
	public ParticleControllerComponent copy () {
		return new RotationInfluencer(this);
	}

}
