package com.baboviolent.game.effect.particle.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.Influencer;
import com.badlogic.gdx.graphics.g3d.particles.values.GradientColorValue;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class RandomBloodColorInfluencer extends Influencer {
	FloatChannel alphaInterpolationChannel;
	FloatChannel lifeChannel;
	public ScaledNumericValue alphaValue;
	FloatChannel colorChannel;
	
	
	public RandomBloodColorInfluencer() {
		alphaValue = new ScaledNumericValue();
		alphaValue.setHigh(1);
	}

	public RandomBloodColorInfluencer(RandomBloodColorInfluencer billboardColorInfluencer) {
		this();
		set(billboardColorInfluencer);
	}

	public void set(RandomBloodColorInfluencer colorInfluencer) {
		this.alphaValue.load(colorInfluencer.alphaValue);
	}
	
	@Override
	public void allocateChannels () {
		colorChannel = controller.particles.addChannel(ParticleChannels.Color);
		//Hack this allows to share the channel descriptor structure but using a different id temporary
		ParticleChannels.Interpolation.id = controller.particleChannels.newId();
		alphaInterpolationChannel = controller.particles.addChannel(ParticleChannels.Interpolation);
		lifeChannel = controller.particles.addChannel(ParticleChannels.Life);
	}
	
	@Override
	public void activateParticles (int startIndex, int count) {
		for(int 	i=startIndex*colorChannel.strideSize, 
						a = startIndex*alphaInterpolationChannel.strideSize,
						l = startIndex*lifeChannel.strideSize + ParticleChannels.LifePercentOffset,
						c = i +count*colorChannel.strideSize; 
						i < c; 
						i +=colorChannel.strideSize, 
						a +=alphaInterpolationChannel.strideSize,
						l +=lifeChannel.strideSize){
			float alphaStart = alphaValue.newLowValue();
			float alphaDiff = alphaValue.newHighValue() - alphaStart;
			colorChannel.data[i+ParticleChannels.RedOffset] = MathUtils.random(0.3f, 1f);
			colorChannel.data[i+ParticleChannels.GreenOffset] = 0;
			colorChannel.data[i+ParticleChannels.BlueOffset] = 0;
			colorChannel.data[i+ParticleChannels.AlphaOffset] = alphaStart + alphaDiff*alphaValue.getScale(lifeChannel.data[l]);
			alphaInterpolationChannel.data[a+ParticleChannels.InterpolationStartOffset] = alphaStart;
			alphaInterpolationChannel.data[a+ParticleChannels.InterpolationDiffOffset] = alphaDiff;
		}
	}

	@Override
	public void update () {
		for(int 	i=0, a = 0, l = ParticleChannels.LifePercentOffset,
			c = i +controller.particles.size*colorChannel.strideSize; 
			i < c; 
			i +=colorChannel.strideSize, a +=alphaInterpolationChannel.strideSize, l +=lifeChannel.strideSize){
			
			float lifePercent = lifeChannel.data[l];
			colorChannel.data[i+ParticleChannels.AlphaOffset] = alphaInterpolationChannel.data[a+ParticleChannels.InterpolationStartOffset] 
				+ alphaInterpolationChannel.data[a+ParticleChannels.InterpolationDiffOffset] *alphaValue.getScale(lifePercent);
		}
	}
	
	@Override
	public RandomBloodColorInfluencer copy () {
		return new  RandomBloodColorInfluencer(this);
	}
	
	@Override
	public void write (Json json) {
		json.writeValue("alpha", alphaValue);
	}
	
	@Override
	public void read (Json json, JsonValue jsonData) {
		alphaValue = json.readValue("alpha", ScaledNumericValue.class, jsonData);
	}
}
