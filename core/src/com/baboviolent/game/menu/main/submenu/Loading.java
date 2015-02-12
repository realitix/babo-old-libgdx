package com.baboviolent.game.menu.main.submenu;

import com.baboviolent.game.menu.main.MainMenu;
import com.baboviolent.game.menu.main.submenu.solo.FastGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Loading extends SubMenu {
	
	public Loading() {
		super();
		skin = new Skin(Gdx.files.internal("data/skin/main_menu/skin.json"));
		Label label = new Label("Chargement", skin);		
		Table table = new Table();
		table.add(label).fill().expand();
		add(table);
	}
}