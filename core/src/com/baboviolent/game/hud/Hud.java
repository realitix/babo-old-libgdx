package com.baboviolent.game.hud;

import com.baboviolent.game.hud.widget.LifeWidget;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class Hud {
	private Stage stage;
	
	public Hud() {
		int width = 1920;
 		int height = 1080;
 		stage = new Stage(new FillViewport(width, height));
 		
 		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
 		
 		// Barre de vie
 		LifeWidget pb = new LifeWidget(0, 100, 1, false, skin);
 		pb.setBounds(100, 900, 300, 300);
 		pb.setValue(50);
 		stage.addActor(pb);
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void update() {
		stage.act(Gdx.graphics.getDeltaTime());        
	}
	
	public void render() {
		stage.draw();
	}
}
