package com.baboviolent.game.map.editor;

public class Menu {
    private Stage stage;
    
    public Menu(final MapEditorScreen editorScreen) {
        // Batch
		SpriteBatch batch = new SpriteBatch();
		
		// Create stage
		stage = new Stage(new ScreenViewport(), batch);
		
		// Create table layout
		Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        // Chargement des textures
        ObjectMap<String, Texture> textures = TextureLoader.getGroundTextures();
        
        // Pour chaque texture, on va créer un widget
        for (ObjectMap.Entry<String, Texture> e : textures.entries()) {
            Image image = new Image(e.value);
            image.addListener(new EditorInputListener(editorScreen, e.key));
            table.add(image);
        }
	}
}