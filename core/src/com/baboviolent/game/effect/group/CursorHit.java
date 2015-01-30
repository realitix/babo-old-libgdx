package com.baboviolent.game.effect.group;

import com.baboviolent.game.effect.BaboEffectSystem;

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
