package com.baboviolent.game.bullet;

import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.utils.Array;

public class BulletContactListener extends ContactListener {
	public final static short PLAYER_FLAG = 1<<8;
	private Array<Babo> babos = new Array<Babo>();
	
	public BulletContactListener(Array<Babo> b) {
		babos = b;
	}
	
	@Override
	public void onContactProcessed(btManifoldPoint cp, int userValue0, boolean match0, int userValue1, boolean match1) {
		System.out.println("userValue0 = "+userValue0);
		System.out.println("userValue1 = "+userValue1);
		System.out.println("match0 = "+match0);
		System.out.println("match1 = "+match1);
	}
}