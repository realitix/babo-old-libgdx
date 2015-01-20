package com.baboviolent.game.effect.group;

import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.light.BaboLightSystem;
import com.baboviolent.game.effect.light.effects.Light1Effect;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.effects.Collision1Effect;
import com.baboviolent.game.effect.particle.effects.MuzzleFlash1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke2Effect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Shoot1 extends GroupEffect {
	public static final String NAME = "Shoot1";
	
	public Shoot1(BaboEffectSystem s) {
		super(s);
	}

	public void start(Matrix4 transform, Vector3 from, Vector3 to, Vector3 normalRay) {
		BaboParticleSystem p = effectSystem.getParticleSystem();		
		p.start(MuzzleFlash1Effect.NAME, transform);
		p.startWithWidth(Smoke1Effect.NAME, transform, from.dst(to));
		
		Vector3 dir = to.cpy().sub(from).nor().scl(20);
		Matrix4 impact = transform.cpy().trn(to.cpy().sub(from).sub(dir));
		p.start(Collision1Effect.NAME, impact);
		p.startWithNormal(Smoke2Effect.NAME, impact, normalRay);
	}	
}
