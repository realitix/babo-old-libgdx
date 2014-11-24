package com.baboviolent.game.mode;

public class BaseMode {
    protected final BulletWorld world;
    protected final String mapName;
    protected Player player;
    
    public BaseMode(final BulletWorld world, final String mapName) {
        this.world = world;
        this.mapName = mapName;
    }
    
    public Player getPlayer() {
        return player;
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
	}
}