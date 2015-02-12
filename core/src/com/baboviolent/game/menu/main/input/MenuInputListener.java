package com.baboviolent.game.menu.main.input;

import com.baboviolent.game.menu.main.MainMenu;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class MenuInputListener implements InputProcessor {

	private MainMenu menu;
	
	public MenuInputListener(MainMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if( keycode == Keys.BACK || keycode == Keys.DPAD_LEFT || keycode == Keys.DEL ) {
			menu.left();
		}
		else if( keycode == Keys.DPAD_UP ) {
			menu.up();
		}
		else if( keycode == Keys.DPAD_DOWN ) {
			menu.down();
		}
		else if( keycode == Keys.DPAD_RIGHT) {
			menu.right();
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
