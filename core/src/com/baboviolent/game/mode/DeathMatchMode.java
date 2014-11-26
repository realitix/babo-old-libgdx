package com.baboviolent.game.mode;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.Utils;
import com.baboviolent.game.bullet.BulletInstance;
import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.gameobject.Babo;
import com.baboviolent.game.map.Map;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class DeathMatchMode extends BaseMode {
	
	public DeathMatchMode(final BulletWorld world, final String mapName) {
		super(world, mapName);
    }
	
	/*
     * Initialise le monde
    */
    public void initWorld() {
        // Ajout de la map
        BulletInstance worldInstance = world.add(Map.loadInstance(mapName));
        
        // Calcul des dimensions
        Vector3 d = Utils.getInstanceDimensions(worldInstance);
        mapDimensions.set(d.x, d.z);
        
        // Ajout du joueur
        player = new Babo("skin03");
        player.translate(new Vector3(800, 1000, 800));
        world.add(player);
    }
    
    /**
     * Analyse le monde
     */ 
	public void update() {
		super.update();
	}
}