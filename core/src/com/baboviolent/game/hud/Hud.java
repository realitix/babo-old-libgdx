package com.baboviolent.game.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class Hud {
	private Stage stage;
	
	public Hud() {
		int width = 1920;
 		int height = 1080;
 		stage = new Stage(new FillViewport(width, height));
 		Table table = new Table();
 		table.setFillParent(true);
 		stage.addActor(table);
	}
	
	public Stage getStage() {
		return stage;
	}
}
