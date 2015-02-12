package com.baboviolent.game.menu.main.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;

public class ContainerGroup extends HorizontalGroup {
	
	private boolean round = true;
	
	@Override
	public void layout () {
		float spacing = super.getSpace();
		float padBottom = super.getPadBottom();
		float padTop = super.getPadTop();
		float padLeft = super.getPadLeft();
		float padRight = super.getPadRight();
		float fill = super.getFill();
		int align = super.getAlign();
		boolean reverse = super.getReverse(), round = this.round;

		float groupHeight = getHeight() - padTop - padBottom;
		float x = !reverse ? padLeft : getWidth() - padRight + spacing;
		SnapshotArray<Actor> children = getChildren();
		for (int i = 0, n = children.size; i < n; i++) {
			LabelContainerGroup child = (LabelContainerGroup) children.get(i);
			float width, height;
			if (child instanceof Layout) {
				Layout layout = (Layout)child;
				if (fill > 0)
					height = groupHeight * fill;
				else
					height = Math.min(layout.getPrefHeight(), groupHeight);
				height = Math.max(height, layout.getMinHeight());
				float maxHeight = layout.getMaxHeight();
				if (maxHeight > 0 && height > maxHeight) height = maxHeight;
				width = layout.getPrefWidth();
			} else {
				width = child.getWidth();
				height = child.getHeight();
				if (fill > 0) height *= fill;
			}

			// Si l'enfant est en manuel, on ne le dirige plus
			float y = child.getY();
			if( !child.getManual() ) {
				y = padBottom;
				if ((align & Align.top) != 0)
					y += groupHeight - height;
				else if ((align & Align.bottom) == 0) // center
					y += (groupHeight - height) / 2;
			}
	
				if (reverse) x -= (width + spacing);
				if (round)
					child.setBounds(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
				else
					child.setBounds(x, y, width, height);
				if (!reverse) x += (width + spacing);

		}
	}
}
