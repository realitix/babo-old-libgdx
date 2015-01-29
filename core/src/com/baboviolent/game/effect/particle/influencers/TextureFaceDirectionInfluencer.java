package com.baboviolent.game.effect.particle.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.ChannelDescriptor;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel;
import com.badlogic.gdx.graphics.g3d.particles.influencers.Influencer;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;


public class TextureFaceDirectionInfluencer  extends Influencer {
	private FloatChannel rotationChannel;
	
	public TextureFaceDirectionInfluencer() {

	}
	
	public TextureFaceDirectionInfluencer(TextureFaceDirectionInfluencer influencer) {
		this();
		set(influencer);
	}
	
	private void set (TextureFaceDirectionInfluencer influencer) {
	}
	
	@Override
	public void allocateChannels() {
		rotationChannel = controller.particles.addChannel(ParticleChannels.Rotation2D);
	}
	
	private float getAngleFromQuaternion(Quaternion q) {
		Vector3 currentAngleAxis = new Vector3();
    	float currentAngle = q.getAxisAngle(currentAngleAxis);
    	
    	if( currentAngleAxis.y < 0 ) {
    		currentAngle = 360 - currentAngle;
    	}
    	
    	return (360 - currentAngle - 90 ) % 360;
	}
	
	private float getTextureRotation() {
		Quaternion tmpQ = new Quaternion();
		this.controller.transform.getRotation(tmpQ);
		return getAngleFromQuaternion(tmpQ);
	}
	
	@Override
	public void activateParticles (int startIndex, int count) {
		float rotation = getTextureRotation();
		float cosBeta = MathUtils.cosDeg(rotation);
		float sinBeta = MathUtils.sinDeg(rotation);
		
		for(int i=startIndex*rotationChannel.strideSize, c = i + count*rotationChannel.strideSize;
				i < c;  
				i+= rotationChannel.strideSize) {
			rotationChannel.data[i+ParticleChannels.CosineOffset] = cosBeta;
			rotationChannel.data[i+ParticleChannels.SineOffset] = sinBeta;
		}
		
	}
	
	@Override
	public void write (Json json) {
	}
	
	@Override
	public void read (Json json, JsonValue jsonData) {
	}

	@Override
	public ParticleControllerComponent copy () {
		return new TextureFaceDirectionInfluencer(this);
	}

}
