package com.baboviolent.game.map.editor;

import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.input.GestureDetector;


/**
 * Cette classe gère les évènements de la souris et du clavier
 * Il permet d'intéragir avec le monde 3d dans l'éditeur'
 */ 
public class EditorInputAdapter extends InputAdapter {
    private MapEditorScreen editorScreen;
    
    public EditorInputAdapter(final MapEditorScreen m) {
    	super();
        editorScreen = m;
    }
    
    public boolean mouseMoved(int screenX, int screenY) {
        editorScreen.mouseMove(screenX, screenY);
        //editorScreen.moveCurrentModelInstance(screenX, screenY);
        return true;
    }
    
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    	if(button != Buttons.LEFT) {
    		return false;
    	}
    	
    	editorScreen.mouseClick(screenX, screenY);
    	return true;
    }
}