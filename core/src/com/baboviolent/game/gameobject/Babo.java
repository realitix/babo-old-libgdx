package com.baboviolent.game.gameobject;

public class Babo {
	private BulletInstance instance;
	
	public Babo(BulletInstance i) {
		instance = i;
	}
    
    public BulletInstance getInstance() {
        return instance;
    }
}