package com.baboviolent.game.effect.particle.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.influencers.Influencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer.AspectTextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Non utilise
 * Peut etre utile plus tard
 *
 */
public class PositionInfluencer extends Influencer {
	private Vector3 tmpV3 = new Vector3();
	private Vector3 tmpV32 = new Vector3();
	
	public ScaledNumericValue strengthValue;
	private FloatChannel positionChannel, interpolationChannel, lifeChannel;
	public float thetaValue;
	public float phiValue;
	
	public PositionInfluencer() {
		strengthValue = new ScaledNumericValue();
		strengthValue.setHigh(1);
	}
	
	public PositionInfluencer(PositionInfluencer influencer) {
		this();
		set(influencer);
		thetaValue = influencer.thetaValue;
		phiValue = influencer.phiValue;
	}
	
	private void set (PositionInfluencer influencer) {
		strengthValue.load(influencer.strengthValue);
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
			float start = strengthValue.newLowValue();
			float diff = strengthValue.newHighValue();
			interpolationChannel.data[a +ParticleChannels.InterpolationStartOffset] = start;
			interpolationChannel.data[a +ParticleChannels.InterpolationDiffOffset] = diff;
		}
		
		tmpV32.setZero().mul(controller.transform);
	}
	
	@Override
	public void update() {
		for(int i = 0, a = 0, l = ParticleChannels.LifePercentOffset,
			c = controller.particles.size*positionChannel.strideSize; 
			i < c; 
			i += positionChannel.strideSize, a += interpolationChannel.strideSize,
			l += lifeChannel.strideSize) {
			
			float strength = interpolationChannel.data[a +ParticleChannels.InterpolationStartOffset] + 
					interpolationChannel.data[a +ParticleChannels.InterpolationDiffOffset] * strengthValue.getScale(lifeChannel.data[l]);
			
			float cosTheta = MathUtils.cosDeg(thetaValue);
			float sinTheta = MathUtils.sinDeg(thetaValue);
			float cosPhi = MathUtils.cosDeg(phiValue);
			float sinPhi = MathUtils.sinDeg(phiValue);
			
			tmpV3.set(cosTheta *sinPhi, cosPhi, sinTheta*sinPhi).nor().scl(strength);	
			positionChannel.data[i +ParticleChannels.XOffset] = tmpV32.x + tmpV3.x;
			positionChannel.data[i +ParticleChannels.YOffset] = tmpV32.y + tmpV3.y;
			positionChannel.data[i +ParticleChannels.ZOffset] = tmpV32.z + tmpV3.z;
		}		
	}
	
	@Override
	public void write (Json json) {
		json.writeValue("valueX", strengthValue);
	}
	
	@Override
	public void read (Json json, JsonValue jsonData) {
		strengthValue = json.readValue("valueX", ScaledNumericValue.class, jsonData);
	}

	@Override
	public ParticleControllerComponent copy () {
		return new PositionInfluencer(this);
	}

}
