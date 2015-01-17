package com.baboviolent.game.effect.group;

import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.effects.Bullet1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke2Effect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Shoot1 extends GroupEffect {
	public static final String NAME = "Shoot1";
	
	public Shoot1(BaboEffectSystem s) {
		super(s);
	}

	public void start(Matrix4 transform, Vector3 from, Vector3 to, int normal) {
		BaboParticleSystem p = effectSystem.getParticleSystem();
		p.start(Bullet1Effect.NAME, transform);
		p.startWithWidth(Smoke1Effect.NAME, transform, from.dst(to));
		Vector3 dir = to.cpy().sub(from).nor().scl(20);
		p.startWithNormal(Smoke2Effect.NAME, transform.cpy().trn(to.cpy().sub(from).sub(dir)), normal);
	}	
}
