package com.baboviolent.game.effect.group;

import com.baboviolent.game.effect.BaboEffectSystem;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class GroupEffect {
	protected BaboEffectSystem effectSystem;
	
	public GroupEffect(BaboEffectSystem s) {
		effectSystem = s;
	}
	
	public void start(Matrix4 transform, Vector3 from, Vector3 to, Vector3 normalRay) {}
}
