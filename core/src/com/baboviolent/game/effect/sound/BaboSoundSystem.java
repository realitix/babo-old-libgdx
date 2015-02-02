package com.baboviolent.game.effect.sound;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class BaboSoundSystem implements Disposable {
	public static final int SHOTGUN = 1;
	
	private ObjectMap<Integer, Sound> sounds;
	
	public BaboSoundSystem() {
		initSystem();
	}
	
	public void initSystem() {
		sounds = new ObjectMap<Integer, Sound>();
		
		String p = BaboViolentGame.PATH_SOUNDS;
		sounds.put(SHOTGUN, Gdx.audio.newSound(Gdx.files.internal(p+"shotgun.wav")));
	}
	
	public void start(int id) {
		sounds.get(id).play(1);
	}

	@Override
	public void dispose() {
		for (ObjectMap.Entry<Integer, Sound> e : sounds.entries()) {
			e.value.dispose();
		}
	}
}
