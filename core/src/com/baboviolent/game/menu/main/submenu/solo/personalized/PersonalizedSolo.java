package com.baboviolent.game.menu.main.submenu.solo.personalized;

import com.baboviolent.game.menu.main.MainMenu;
import com.baboviolent.game.menu.main.submenu.SubMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PersonalizedSolo extends SubMenu {
	public final static int ACTION_START = 1;
	
	public PersonalizedSolo(final MainMenu menu) {
		super(menu);
		init();
	}
	
	private void init() {
		skin = new Skin(Gdx.files.internal("data/skin/main_menu/submenu/skin.json"));
		
		AiSelector aiSelector = new AiSelector(skin);
		
		Table table = new Table();
		table.add(aiSelector).fill().expand();
		
		add(table);
	}

	@Override
	public void startAction(int action) {
		switch(action) {
			case ACTION_START:
				menu.getMenuScreen().startGame();
		}
	}
}
