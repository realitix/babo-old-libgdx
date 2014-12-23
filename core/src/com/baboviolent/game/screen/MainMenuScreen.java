package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
	final BaboViolentGame game;
    PerspectiveCamera camera;
    private Stage stage;

    public MainMenuScreen(final BaboViolentGame g) {
        game = g;
        camera = new PerspectiveCamera();
        
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
		 
		// Boutons
		TextButton soloWidget = new TextButton("Solo", skin);
		TextButton multiplayerWidget = new TextButton("Multi", skin);
		TextButton editorWidget = new TextButton("Editeur", skin);
         
        // Listener
        soloWidget.addListener(
     		new ClickListener() {
     			public void clicked (InputEvent event, float x, float y) {
     				game.setScreen(new GameScreen(game, GameScreen.TYPE_SOLO));
     			}
     	});
        
        multiplayerWidget.addListener(
     		new ClickListener() {
     			public void clicked (InputEvent event, float x, float y) {
     				game.setScreen(new GameScreen(game, GameScreen.TYPE_MULTIPLAYER));
     			}
     	});
        
        
        editorWidget.addListener(
     		new ClickListener() {
     			public void clicked (InputEvent event, float x, float y) {
     				game.setScreen(new MapEditorScreen(game));
     			}
     	});
        
        table.add(soloWidget);
        table.add(multiplayerWidget);
        table.add(editorWidget);
        
        Gdx.input.setInputProcessor(stage);
         
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());        
        stage.draw();
    }
    
	@Override
	public void resize(int width, int height) {
	}
	 
	@Override
	public void show() {
	}
	 
	@Override
	public void hide() {
	}
	 
	@Override
	public void pause() {
	}
	 
	@Override
	public void resume() {
	}
	 
	@Override
	public void dispose() {
	}
}
