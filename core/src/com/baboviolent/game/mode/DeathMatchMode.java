package com.baboviolent.game.mode;

import com.baboviolent.game.BaboViolentGame;
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
        BoundingBox bb = new BoundingBox();
        worldInstance.calculateBoundingBox(bb);
        Vector3 d = new Vector3();
        bb.getDimensions(d);
        mapDimensions.set(d.x, d.z);
        
        // Ajout du joueur
        player = new Babo(world.add(BaboViolentGame.BABO_MODEL_NAME, 100, 100, 100));
    }
    
    /**
     * Analyse le monde
     */ 
	public void update() {
	}
}