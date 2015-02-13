package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.menu.extra.MenuBackground;
import com.baboviolent.game.menu.main.MainMenu;
import com.baboviolent.game.shader.MenuShader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class MainMenuScreen implements Screen {
	final BaboViolentGame game;
    private MainMenu menu;
    private MenuBackground mb;
    private float alphaBackground = 1;
    
    public MainMenuScreen(final BaboViolentGame g) {
    	menu = new MainMenu(this);
        game = g;
        mb = new MenuBackground();
    }
    
    public void startGame() {
    	game.setScreen(new GameScreen(game, GameScreen.TYPE_SOLO));
    	dispose();
    }
    
    public void startEditor() {
    	game.setScreen(new MapEditorScreen(game));
    	dispose();
    }
    
    public void setAlphaBackground(float alpha) {
    	alphaBackground = alpha;
    }
    
    @Override
    public void render(float delta) {
    	Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        mb.render(alphaBackground);
        menu.update();
        menu.render();
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
		mb.dispose();
		menu.dispose();
	}
}
