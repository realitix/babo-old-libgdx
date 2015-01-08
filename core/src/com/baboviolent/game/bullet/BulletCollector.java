package com.baboviolent.game.bullet;

import com.baboviolent.game.gameobject.GameObject;
import com.badlogic.gdx.utils.ObjectMap;

public class BulletCollector {
	public static final ObjectMap<Integer, GameObject> objects = new ObjectMap<Integer, GameObject>();

	public static GameObject get(int id) {
		return objects.get(id, null);
	}
	
	public static void add(int id, GameObject object) {
		objects.put(id, object);
	}
}
