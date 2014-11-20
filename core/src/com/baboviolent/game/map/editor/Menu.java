package com.baboviolent.game.map.editor;

import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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
        
        // Menu classique
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        
        // On créé les boutons
        final TextField mapNameWidget = new TextField("", skin);
        Label saveWidget = new Label("Enregistrer", skin).addListener();
        
        table.add(mapNameWidget);
        table.add(
        	saveWidget.addListener(new ClickListener() {
	    		public void clicked (InputEvent event, float x, float y) {
	    			editorScreen.saveMap(mapNameWidget.getText());
	    		}
	    }));
        
        // Chargement des textures
        ObjectMap<String, Texture> textures = TextureLoader.getGroundTextures();
        
        // Pour chaque texture, on va créer un widget
        for (final ObjectMap.Entry<String, Texture> e : textures.entries()) {
            /*Image image = new Image(e.value);
            //image.addListener(new EditorInputListener(editorScreen, e.key));
            image.addListener(new ClickListener() {
            	public void clicked (InputEvent event, float x, float y) {
            		editorScreen.selectGround(e.key);
            	}
            });
            table.add(image);*/
            
            table.add(new Image(e.value)
            	.addListener(new ClickListener() {
            		public void clicked (InputEvent event, float x, float y) {
            			editorScreen.selectGround(e.key);
            		}
            	})
            );
        }
	}
	
	public void render() {
	    stage.act(Gdx.graphics.getDeltaTime());        
        stage.draw();
	}
}