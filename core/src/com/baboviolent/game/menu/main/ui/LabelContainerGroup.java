package com.baboviolent.game.menu.main.ui;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

public class LabelContainerGroup extends VerticalGroup {
	private boolean manual;
	
	public LabelContainerGroup setManual(boolean m) {
		manual = m;
		return this;
	}
	
	public boolean getManual() {
		return manual;
	}
}
