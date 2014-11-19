package com.baboviolent.game.map.editor;

import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EditorInputListener extends ClickListener {
    private MapEditorScreen editorScreen;
    private String type;
    
    public EditorInputListener (final MapEditorScreen e, String type) {
    	super();
    	
    	this.editorScreen = e;
        this.type = type;
    }
    
    public void clicked (InputEvent event, float x, float y) {
        editorScreen.selectGround(type);
    }
}