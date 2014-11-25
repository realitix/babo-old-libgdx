package com.baboviolent.game.gameobject;

import com.baboviolent.game.bullet.BulletInstance;

public class Babo {
	private BulletInstance instance;
	
	public Babo(BulletInstance i) {
		instance = i;
	}
    
    public BulletInstance getInstance() {
        return instance;
    }
}