package com.baboviolent.game.effect.light.effects;

import com.baboviolent.game.effect.particle.effects.Collision1Effect;

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
		color.set(255, 255, 255, 1);
		intensity = 1;
		life = 2000;
	}
}
