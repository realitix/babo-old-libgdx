package com.baboviolent.game.effect.group;

import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.light.effects.Light1Effect;
import com.baboviolent.game.effect.light.effects.Light2Effect;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.effects.Bullet1Effect;
import com.baboviolent.game.effect.particle.effects.Collision1Effect;
import com.baboviolent.game.effect.particle.effects.MuzzleFlash1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke2Effect;
import com.baboviolent.game.effect.sound.BaboSoundSystem;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Shoot1 extends GroupEffect {
	public static final String NAME = "Shoot1";
	
	public Shoot1(BaboEffectSystem s) {
		super(s);
	}

	@Override
	public void start(Matrix4 transform, Vector3 from, Vector3 to, Vector3 normalRay) {
		BaboParticleSystem p = effectSystem.getParticleSystem();
		p.startWithWidth(Bullet1Effect.NAME, transform, from.dst(to));
		p.startWithWidth(Smoke1Effect.NAME, transform, from.dst(to));
		effectSystem.getLightSystem().start(Light2Effect.NAME, transform, from.dst(to));
		
		Vector3 dir = to.cpy().sub(from).nor().scl(0.2f);
		Matrix4 impact = transform.cpy().trn(to.cpy().sub(from).sub(dir));
		p.start(Collision1Effect.NAME, impact);
		p.startWithNormal(Smoke2Effect.NAME, impact, normalRay);
	}
	
	@Override
	public void startUnique(Matrix4 transform) {
		//effectSystem.getLightSystem().start(Light1Effect.NAME, transform);
		effectSystem.getParticleSystem().start(MuzzleFlash1Effect.NAME, transform);
		effectSystem.getSoundSystem().start(BaboSoundSystem.SHOTGUN);
	}
}
