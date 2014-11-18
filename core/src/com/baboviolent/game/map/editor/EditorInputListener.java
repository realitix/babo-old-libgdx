package com.baboviolent.game.map.editor;

public class EditorInputListener extends ClickListener {
    private MapEditorScreen editorScreen;
    private String type;
    
    public EditorInputListener (final MapEditorScreen e, String type) {
        this.editorScreen = e;
        this.type = type;
        super();
    }
    
    public void clicked (InputEvent event, float x, float y) {
        editorScreen.selectGround(type);
        return true;
    }
}