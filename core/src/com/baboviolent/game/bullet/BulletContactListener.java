package com.baboviolent.game.bullet;

import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.utils.Array;

public class BulletContactListener extends ContactListener {
	public final static short PLAYER_FLAG = 1<<8;
	private Array<Babo> babos = new Array<Babo>();
	
	public BulletContactListener(Array<Babo> b) {
		babos = b;
	}
	
	@Override
	public void onContactStarted(int userValue0, boolean match0, int userValue1, boolean match1) {
		int playerId = (match1) ? userValue0 : userValue1;
		int power = (match0) ? userValue0 : userValue1;
		
		// UserValue0 contient l'id de babo
		// UserValue1 contient la puissance de la balle (l'énergie à enlever à babo)
		Babo babo = null;
		for( int i = 0; i < babos.size; i++ ) {
			if( babos.get(i).getId() == playerId ) {
				babo = babos.get(i);
			}
		}
		babo.hit(power);
	}
}