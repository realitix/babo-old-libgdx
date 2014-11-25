package com.baboviolent.game.map.editor;

import com.baboviolent.game.loader.BaboModelLoader;
import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.map.Map;
import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
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
        table.left().top();
        table.setDebug(true);
        
        // Menu classique
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        
        // On créé les boutons
        final TextField mapNameWidget = new TextField("", skin);
        Label saveWidget = new Label("Enregistrer", skin);
        Label loadWidget = new Label("Charger", skin);
        Label clearWidget = new Label("Effacer", skin);
        TextButton exitWidget = new TextButton("Quitter", skin);
        
        // Listener
        saveWidget.addListener(
    		new ClickListener() {
    			public void clicked (InputEvent event, float x, float y) {
    				editorScreen.saveMap(mapNameWidget.getText());
    			}
    	});
    	
    	loadWidget.addListener(
    		new ClickListener() {
    			public void clicked (InputEvent event, float x, float y) {
    				editorScreen.loadMap(mapNameWidget.getText());
    			}
    	});
    	
    	clearWidget.addListener(
    		new ClickListener() {
    			public void clicked (InputEvent event, float x, float y) {
    				editorScreen.selectEraser();
    			}
    	});
    	
        exitWidget.addListener(
    		new ClickListener() {
    			public void clicked (InputEvent event, float x, float y) {
    				Gdx.app.exit();
    			}
    	});
        
        table.add(exitWidget);
        table.add(clearWidget);
        table.add(mapNameWidget);
        table.add(saveWidget);
        table.add(loadWidget);
        table.row();
        
        // Chargement des textures dans le bon ordre
        Array<String> tsGround = TextureLoader.listTextureFolder(Map.TYPE_GROUND);
        ObjectMap<String, Texture> texturesGround = TextureLoader.getTextures(Map.TYPE_GROUND);
        Array<String> tsWall = TextureLoader.listTextureFolder(Map.TYPE_WALL);
        ObjectMap<String, Texture> texturesWall = TextureLoader.getTextures(Map.TYPE_WALL);
        for( int i = 0; i < tsGround.size; i++ ) {
        	Image image = new Image(texturesGround.get(tsGround.get(i)));
        	image.setScaling(Scaling.fit);
            image.addListener(new BaboInputListener(editorScreen, tsGround.get(i), Map.TYPE_GROUND));
            table.add(image).fill();
            
            if(i%2 != 0) {
            	Image imageWall = new Image(texturesWall.get(tsWall.get(i/2)));
            	imageWall.setScaling(Scaling.fit);
                imageWall.addListener(new BaboInputListener(editorScreen, tsWall.get(i/2), Map.TYPE_WALL));
                table.add(imageWall).fill();
            	table.row();
            }
        }
        
        // Chargement des modèles
        table.row();
        Array<String> models = BaboModelLoader.listModelFolder();
        for (int i = 0; i < models.size; i++) {
            Label l = new Label(models.get(i), skin);
            l.addListener(new BaboInputListener(editorScreen, models.get(i), Map.TYPE_OBJECT));
            table.add(l);
        }
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void render() {
	    stage.act(Gdx.graphics.getDeltaTime());        
        stage.draw();
	}
	
	public class BaboInputListener extends ClickListener {
    	private String name;
    	private String type;
    	private final MapEditorScreen editorScreen;
    	public BaboInputListener(final MapEditorScreen s, String name, String type) {
    		this.name = name;
    		this.type = type;
    		this.editorScreen = s;
    	}
    	public void clicked (InputEvent event, float x, float y) {
    		if(type == Map.TYPE_GROUND) {
    			editorScreen.selectGround(name);
    		}
    		else if(type == Map.TYPE_WALL) {
    			editorScreen.selectWall(name);
    		}
    		else if(type == Map.TYPE_OBJECT) {
    			editorScreen.selectObject(name);
    		}
    	}
    }
}