package com.baboviolent.game.mode;

public class DeathMatchMode extends BaseMode {
	
	/*
     * Initialise le monde
    */
    public void initWorld() {
        // Ajout de la map
        world.add(Map.loadInstance(mapName));
        
        // Ajout du joueur
        player = new Player(world.add(BaboViolentGame.BABO_MODEL_NAME, 100, 100, 100));
    }
    
    /**
     * Analyse le monde
     */ 
	public void update() {
	}
}