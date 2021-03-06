package com.baboviolent.game.effect.light.effects;

import com.badlogic.gdx.utils.TimeUtils;

public class Light1Effect extends BaboLightEffect {
	public static final String NAME = "Light1Effect";
	
	public Light1Effect() {
		super();
		configure();
	}
	
	public Light1Effect(Light1Effect effect) {
		super(effect);
	}
	
	@Override
	public Light1Effect copy() {
		return new Light1Effect(this);
	}
	
	public void configure() {
		color.set(0.8f, 0.34f, 0, 1);
		intensity = 10;
		life = 200;
	}
	
	@Override
	public void update() {
		super.update();
		light.intensity = intensity * (life - TimeUtils.timeSinceMillis(startTime))/life;
	}
}



