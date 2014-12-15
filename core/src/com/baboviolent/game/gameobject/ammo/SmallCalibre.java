package com.baboviolent.game.gameobject.ammo;

import com.baboviolent.game.gameobject.GameObject;
import com.baboviolent.game.gameobject.weapon.Weapon;

public class SmallCalibre extends Ammo {
	
	public SmallCalibre(final Weapon w) {
		super(w);
		
		name = "SmallCalibre";		
	    friction = 0;
        rollingFriction = 0;
        linearDamping = 0;
        angularDamping = 0;
        restitution = 0;
        mass = 40;
        
        expireTime = 5000;
        power = 10;
        super.init();
	}
}