package com.baboviolent.game.menu.main.submenu.solo.personalized;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

public class AiSelector extends HorizontalGroup {

	public AiSelector(Skin skin) {
		init(skin);
	}
	
	private void init(Skin skin) {
		Label title = new Label("Ennemis: 0", skin);
		this.addActor(title);
		
		Slider slider = new Slider(0, 9, 1, false, skin);
		this.addActor(slider);
	}
}
