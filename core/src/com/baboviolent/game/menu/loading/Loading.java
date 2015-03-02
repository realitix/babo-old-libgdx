package com.baboviolent.game.menu.loading;

import com.baboviolent.game.menu.main.MainMenu;
import com.baboviolent.game.menu.main.submenu.solo.FastGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;

public class Loading extends Table implements Disposable {
	
	private Label label;
	private Skin skin;
	
	public Loading() {
		super();
		setFillParent(true);
		skin = new Skin(Gdx.files.internal("data/skin/main_menu/skin.json"));
		label = new Label("Chargement", skin);		
		Table table = new Table();
		table.add(label).fill().expand();
		add(table);
	}
	
	public void setPercent(int percent) {
		label.setText("Chargement..."+percent+"%");
	}
	
	@Override
	public void dispose() {
		skin.dispose();
	}
}