package com.baboviolent.game.effect.group;

import com.baboviolent.game.effect.BaboEffectSystem;
import com.baboviolent.game.effect.decal.BaboDecalSystem;
import com.baboviolent.game.effect.light.effects.Light1Effect;
import com.baboviolent.game.effect.particle.BaboParticleSystem;
import com.baboviolent.game.effect.particle.effects.Bullet1Effect;
import com.baboviolent.game.effect.particle.effects.Collision1Effect;
import com.baboviolent.game.effect.particle.effects.MuzzleFlash1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke1Effect;
import com.baboviolent.game.effect.particle.effects.Smoke2Effect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class CursorHit extends GroupEffect {
	public static final String NAME = "CursorHit";
	
	public CursorHit(BaboEffectSystem s) {
		super(s);
	}

	@Override
	public void start() {
		effectSystem.getDecalSystem().cursorHit();
	}
}
