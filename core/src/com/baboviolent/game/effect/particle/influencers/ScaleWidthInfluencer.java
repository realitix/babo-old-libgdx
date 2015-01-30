package com.baboviolent.game.effect.particle.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.influencers.Influencer;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Le systeme de particule de libgdx ne permet pas de modifier une seule composante de scale
 * Dans BillboardParticleBatch ligne 370 on voit que le scale est le meme pour width et height
 * Cependant, un hack possible est de modifier le regionChannel[regionOffset +ParticleChannels.HalfWidthOffset]
 * On peut le changer avec le TextureRegionChannel
 * A tester
 * @author realitix
 *
 */
public class ScaleWidthInfluencer  extends Influencer {
	
	public ScaledNumericValue value;
	private FloatChannel regionChannel, interpolationChannel, lifeChannel;
	
	public ScaleWidthInfluencer() {
		value = new ScaledNumericValue();
		value.setHigh(1);
	}
	
	public ScaleWidthInfluencer(ScaleWidthInfluencer influencer) {
		this();
		set(influencer);
	}
	
	private void set (ScaleWidthInfluencer influencer) {
		value.load(influencer.value);
	}
	
	@Override
	public void allocateChannels() {
		regionChannel = controller.particles.addChannel(ParticleChannels.TextureRegion);
		ParticleChannels.Interpolation.id = controller.particleChannels.newId();
		interpolationChannel = controller.particles.addChannel(ParticleChannels.Interpolation);
		lifeChannel = controller.particles.addChannel(ParticleChannels.Life);
	}
	
	@Override
	public void activateParticles (int startIndex, int count) {		
		for(int i = startIndex*regionChannel.strideSize, a = startIndex*interpolationChannel.strideSize, c = i +count*regionChannel.strideSize; 
			i < c;  i += regionChannel.strideSize, a+=interpolationChannel.strideSize){
			float start = value.newLowValue();
			float diff = value.newHighValue();
			interpolationChannel.data[a +ParticleChannels.InterpolationStartOffset] = start;
			interpolationChannel.data[a +ParticleChannels.InterpolationDiffOffset] = diff;
		}
	}
	
	@Override
	public void update() {
		for(int i = 0, a = 0, l = ParticleChannels.LifePercentOffset,
			c = controller.particles.size*regionChannel.strideSize; 
			i < c; 
			i += regionChannel.strideSize, a += interpolationChannel.strideSize,
			l += lifeChannel.strideSize) {
			
			float val = interpolationChannel.data[a +ParticleChannels.InterpolationStartOffset] + 
					interpolationChannel.data[a +ParticleChannels.InterpolationDiffOffset] * value.getScale(lifeChannel.data[l]);

			regionChannel.data[i +ParticleChannels.HalfWidthOffset] = val/2; 
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
		return new ScaleWidthInfluencer(this);
	}

}
