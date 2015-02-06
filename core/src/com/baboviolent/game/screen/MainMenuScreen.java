package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.menu.main.MainMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class MainMenuScreen implements Screen {
	final BaboViolentGame game;
    private PerspectiveCamera camera;
    private Stage stage;
    private MainMenu menu;
    
    public MainMenuScreen(final BaboViolentGame g) {
    	menu = new MainMenu();
        game = g;
        camera = new PerspectiveCamera();
        
        // Batch
 		SpriteBatch batch = new SpriteBatch();
 		
 		// Create stage
 		int width = 1920;
 		int height = 1080;
 		stage = new Stage(new FillViewport(width, height), batch);
 		
 		// Creation de deux groupes, un pour l'image de fond, l'autre pour les boutons
 		Group background = new Group();
 		background.setBounds(0, 0, width, height);
 		Group foreground = new Group();
 		foreground.setBounds(0, 0, width, height);
 		stage.addActor(background);
 		stage.addActor(foreground);
 		
 		// Background
 		background.addActor(new Image(new Texture(Gdx.files.internal("data/menu/main_menu/background.png"))));
 		
 		
 		// Foreground
 		Table table = new Table();
        table.setFillParent(true);
 		table.center();
        //table.setDebug(true);
        
        // Solo
        ImageButton soloButton = new ImageButton(
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_solo.png")))),
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_solo_hover.png")))),
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_solo_checked.png"))))
        		);
        
        soloButton.addListener(
         		new ClickListener() {
         			public void clicked (InputEvent event, float x, float y) {
         				game.setScreen(new GameScreen(game, GameScreen.TYPE_SOLO));
         			}
         	});
        table.add(soloButton).center().padBottom(50);
        table.row();
        
        // Multi
        ImageButton multiButton = new ImageButton(
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_multi.png")))),
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_multi_hover.png")))),
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_multi_checked.png"))))
        		);
        
        multiButton.addListener(
         		new ClickListener() {
         			public void clicked (InputEvent event, float x, float y) {
         				game.setScreen(new GameScreen(game, GameScreen.TYPE_MULTIPLAYER));
         			}
         	});
        table.add(multiButton).center().padBottom(50);
        table.row();
        
        // Editeur
        ImageButton optionsButton = new ImageButton(
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_options.png")))),
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_options_hover.png")))),
        		new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("data/menu/main_menu/button_options_checked.png"))))
        		);
        
        optionsButton.addListener(
         		new ClickListener() {
         			public void clicked (InputEvent event, float x, float y) {
         				game.setScreen(new MapEditorScreen(game));
         			}
         	});

        table.add(optionsButton).center();        
        
        foreground.addActor(table);
        
        //Gdx.input.setInputProcessor(stage);
         
    }
    
    @Override
    public void render(float delta) {
    	Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menu.update();
        menu.render();
        /*stage.act(Gdx.graphics.getDeltaTime());        
        stage.draw();*/
    }
    
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
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
