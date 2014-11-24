package com.baboviolent.game.mode;

public class DeathMatchMode extends BaseMode {
	
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
        player = new Player(world.add(BaboViolentGame.BABO_MODEL_NAME, 100, 100, 100));
    }
    
    /**
     * Analyse le monde
     */ 
	public void update() {
	}
}