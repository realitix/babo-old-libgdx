package com.baboviolent.game.menu.main.input;

import com.baboviolent.game.menu.main.MainMenu;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class MenuGestureListener implements GestureListener {

	private MainMenu menu;
	private boolean panning;
	private Vector2 panStart = new Vector2();
	private Vector2 panDelta = new Vector2();
	private int minPan = 50;
	
	public MenuGestureListener(MainMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if( !panning ) {
			panStart.set(x, y);
			panning = true;
		}
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		panDelta.set(x,y).sub(panStart);
		
		if( Math.abs(panDelta.x) >= minPan && Math.abs(panDelta.x) > Math.abs(panDelta.y) ) {
			if( panDelta.x > 0 ) 
				menu.left();
			else
				menu.right();
		}
		if( Math.abs(panDelta.y) >= minPan && Math.abs(panDelta.y) > Math.abs(panDelta.x) ) {
			if( panDelta.y > 0 ) 
				menu.up();
			else
				menu.down();
		}
		
		panning = false;
		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
