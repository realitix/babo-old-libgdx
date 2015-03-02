package com.baboviolent.game.effect.light.effects;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class Light2Effect extends BaboLightEffect {
	public static final String NAME = "Light2Effect";
	
	private Quaternion tmpQ = new Quaternion();
	private Vector3 tmpV3 = new Vector3();
	
	private float width;
	
	public Light2Effect() {
		super();
		configure();
	}
	
	public Light2Effect(Light2Effect effect) {
		super(effect);
	}
	
	@Override
	public Light2Effect copy() {
		return new Light2Effect(this);
	}
	
	public void configure() {
		color.set(0.8f, 0.35f, 0, 1);
		intensity = 5;
		life = 300;
	}
	
	@Override
	public void setWidth(float w) {
		this.width = w;
	}
	
	@Override
	public void update() {
		super.update();
		transform.getRotation(tmpQ);
		transform.getTranslation(light.position);
		light.position.y = 0.01f;
		
		tmpV3.set(1, 0, 0);
		tmpV3.mul(tmpQ);
		
		float percent = 1f - (life - TimeUtils.timeSinceMillis(startTime))/(float)life;
		
		light.position.add(tmpV3.scl(width*percent));
		light.intensity = intensity*(1f - percent);
		light.color.set(0.8f, 0.35f* (1f - percent), 0, 1);
	}
}



