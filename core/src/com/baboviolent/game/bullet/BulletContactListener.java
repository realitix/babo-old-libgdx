package com.baboviolent.game.bullet;

import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.ammo.Ammo;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.utils.Array;

public class BulletContactListener extends ContactListener {
	public final static short BABO_FLAG = 1<<8;
	private static Array<GameObject> objects = new Array<GameObject>();
	
	public BulletContactListener() {
	}
	
	public static void addObject(GameObject o) {
		objects.add(o);
	}
	
	public static void removeObject(GameObject o) {
		objects.removeValue(o, true);
	}
	
	@Override
	public void onContactStarted(int userValue0, boolean match0, int userValue1, boolean match1) {
		Babo babo = null;
		Ammo ammo = null;
		for( int i = 0; i < objects.size; i++ ) {
			if( objects.get(i).getId() == userValue0 || objects.get(i).getId() == userValue1 ) {
				if( objects.get(i).getType() == GameObject.TYPE_BABO ) {
					babo = (Babo) objects.get(i);
				}
				if( objects.get(i).getType() == GameObject.TYPE_AMMO ) {
					ammo = (Ammo) objects.get(i);
				}
			}
		}
		
		// On ne peut pas se toucher
		if( babo != ammo.getWeapon().getBabo() ) {
			babo
				.setLastShooter(ammo.getWeapon().getBabo())
				.hit(ammo.getPower());
		}
	}
}