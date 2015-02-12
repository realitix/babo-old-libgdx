package com.baboviolent.game.menu.main.submenu;

import com.baboviolent.game.menu.main.MainMenu;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;

public class SubMenu extends Table implements Disposable {

	protected final MainMenu menu;
	protected Skin skin;
	
	public SubMenu() {
		super();
		menu = null;
		setFillParent(true);
	}
	
	public SubMenu(final MainMenu menu) {
		super();
		this.menu = menu;
		setFillParent(true);
	}

	public void startAction(int action) {
		
	}

	@Override
	public void dispose() {
		skin.dispose();
	}
}
