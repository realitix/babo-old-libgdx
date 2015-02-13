package com.baboviolent.game.menu.main.submenu.solo;

import com.baboviolent.game.menu.main.MainMenu;
import com.baboviolent.game.menu.main.submenu.SubMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Editor extends SubMenu {
	public final static int ACTION_START = 1;
	
	public Editor(final MainMenu menu) {
		super(menu);
		skin = new Skin(Gdx.files.internal("data/skin/main_menu/skin.json"));
		
		Label label = new Label("Démarrer", skin);
		final Editor me = this;
		label.addListener( new ClickListener() { 
			public void clicked (InputEvent event, float x, float y) {
				menu.loadingForAction(me, ACTION_START);
     	}});
		
		Table table = new Table();
		table.add(label).fill().expand();
		
		add(table);
	}

	@Override
	public void startAction(int action) {
		switch(action) {
			case ACTION_START:
				menu.getMenuScreen().startEditor();
		}
	}
}
