package com.baboviolent.game.menu.extra;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class AnimatedText extends HorizontalGroup {
	private String text;
	private Skin skin;
	private Array<Label> labels = new Array<Label>();
	private float cursor; // Avancement de l'effet
	private float animationTime = 2;
	private float time;
	
	public AnimatedText(String text, Skin skin) {
		super();
		this.text = text;
		this.skin = skin;
		initLabel();
	}
	
	private void initLabel() {
		for (int i = 0; i < text.length(); i++) {   
		    String c = Character.toString(text.charAt(i));
		    Label l = new Label(c, skin);
		    l.setColor(1,0,0,1);
		    labels.add(l);
		    addActor(l);
		}
	}
	
	@Override
	public void act(float delta) {
		time += delta;
		boolean complete = time >= animationTime;
		float percent;
		if (complete) {
			percent = 1;
			time = 0;
		}
		else {
			percent = time / animationTime;
		}
		
		float width = this.getPrefWidth();
		cursor = width*percent;
	}
}
