package com.baboviolent.game.effect.light.effects;

import com.baboviolent.game.effect.particle.effects.Collision1Effect;
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
		color.set(1, 0, 0, 0.5f);
		intensity = 100000;
		life = 200;
	}
	
	@Override
	public void update() {
		super.update();
		light.intensity = intensity * (life - TimeUtils.timeSinceMillis(startTime))/life;
	}
}
