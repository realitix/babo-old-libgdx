package com.baboviolent.game.menu.main.ui;

import com.baboviolent.game.menu.main.MainMenu;
import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuStage extends Stage {
	public final static float maxTouchdistance = 20;
	private final MainMenu menu;
	
	public MenuStage(final MainMenu menu) {
		super();
		this.menu = menu;
	}
	
	public MenuStage(Viewport viewport, Batch batch, final MainMenu menu) {
		super(viewport, batch);
		this.menu = menu;
	}

	public MenuStage(Viewport viewport, final MainMenu menu) {
		super(viewport);
		this.menu = menu;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		// Si l'opacite du stage diminue, on diminue l'opacite de l'ecran anime
		menu.getMenuScreen().setAlphaBackground(getRoot().getColor().a);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return super.touchDown(screenX, screenY, pointer, button);		
    }
	
	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		return false;
    }
}