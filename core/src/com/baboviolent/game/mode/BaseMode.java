package com.baboviolent.game.mode;

import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.math.Vector2;

public class BaseMode {
    protected final BulletWorld world;
    protected final String mapName;
    protected Babo player;
    protected Vector2 mapDimensions;
    
    public BaseMode(final BulletWorld world, final String mapName) {
        this.world = world;
        this.mapName = mapName;
    }
    
    public Babo getPlayer() {
        return player;
    }
    
    public Vector2 getMapDimensions() {
        return mapDimensions;
    }
    
    /*
     * Initialise le monde
    */
    public void initWorld() {
    }
    
    /**
     * Analyse le monde
     */ 
	public void update() {
	    player.update();
	}
}