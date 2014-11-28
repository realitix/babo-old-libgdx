package com.baboviolent.game.gameobject.ammo;

public class SmallCalibre extends Ammo {
	public SmallCalibre() {
		super();
		
		name = "SmallCalibre";
		type = GameObject.TYPE_AMMO;
		
	    friction = 0;
        rollingFriction = 0;
        linearDamping = 0;
        angularDamping = 0;
        restitution = 0;
        mass = 1;
        super.init();
	}
}