package com.baboviolent.game.map.editor;

import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.InputAdapter;


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
    
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        editorScreen.mouseClick(screenX, screenY);
        //editorScreen.createCell(screenX, screenY);
        return true;
    }
}