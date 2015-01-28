package com.baboviolent.game.effect.group;

import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.light.BaboLightSystem;
import com.baboviolent.game.effect.light.effects.Light1Effect;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.effects.Blood1Effect;
import com.baboviolent.game.effect.particle.effects.Bullet1Effect;
import com.baboviolent.game.effect.particle.effects.Collision1Effect;
import com.baboviolent.game.effect.particle.effects.MuzzleFlash1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke2Effect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Blood1 extends GroupEffect {
	public static final String NAME = "Blood1";
	
	private Vector3 tmpV3 = new Vector3();
	
	
	public Blood1(BaboEffectSystem s) {
		super(s);
	}

	@Override
	public void start(Matrix4 transform, float damage) {
		transform.getTranslation(tmpV3);
		effectSystem.getDecalSystem().generateBlood(tmpV3, damage);
	}
}
