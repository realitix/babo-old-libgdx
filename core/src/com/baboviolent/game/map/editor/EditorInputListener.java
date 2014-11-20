package com.baboviolent.game.map.editor;

import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Cette classe génère un évènement lors du clic sur
 * un objet ou texture à ajouter
 * 
 * Il permet de gérer à la fois les actions et les textures,
 * en fonction des paramètres passés
 */ 
public class EditorInputListener extends ClickListener {
    public static int ACTION_SAVE = 1;
    private MapEditorScreen editorScreen;
    private String textureType;
    private int action = 0;
    
    public EditorInputListener (final MapEditorScreen e, String type) {
    	super();
    	
    	this.editorScreen = e;
        this.textureType = type;
    }
    
    public EditorInputListener (final MapEditorScreen e, int action) {
    	super();
    	
    	this.editorScreen = e;
        this.action = action;
    }
    
    public void clicked (InputEvent event, float x, float y) {
        switch(action) {
            case EditorInputListener.ACTION_SAVE:
                break;
            default:
                editorScreen.selectGround(textureType);
        }
    }
}