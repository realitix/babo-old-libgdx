package com.baboviolent.game.menu.main;

import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuStage extends Stage {
	public float maxTouchdistance = 20;
	
	public MenuStage() {
		super();
	}
	
	public MenuStage(Viewport viewport, Batch batch) {
		super(viewport, batch);
	}

	public MenuStage(Viewport viewport) {
		super(viewport);
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