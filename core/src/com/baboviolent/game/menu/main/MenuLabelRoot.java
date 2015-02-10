package com.baboviolent.game.menu.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class MenuLabelRoot extends MenuLabel {
	private MenuLabel selectedLabel;
	
	// Constructeur du noeud root
	public MenuLabelRoot(Skin skin) {
		children = new Array<MenuLabel>();
		childrenGroup = new LabelContainerGroup();
		this.skin = skin;
	}

	private MenuLabelRoot getRoot() {
		return this;
	}
	
	@Override
	protected boolean isRoot() {
		return true;
	}

	public void setSelectedLabel(MenuLabel l) {
		selectedLabel = l;
	}
	
	public MenuLabel getSelectedLabel() {
		return selectedLabel;
	}
	
	public void compute() {		
		childrenGroup.align(Align.left);
		for( int i = 0; i < children.size; i++ ) {
			MenuLabel child = children.get(i);
			child.setSkin(skin).compute();
			childrenGroup.addActor(child.getLabel());
		}
	}
	
	public void computeGroup() {		
		for( int i = 0; i < children.size; i++ ) {
			children.get(i).computeGroup();
		}
	}
	
	public void computeInitX() {
		ContainerGroup container = (ContainerGroup) childrenGroup.getParent();
		// @TODO a definir en fonction du ratio
		float offsetX = 250;
		container.setX(MainMenu.width - childrenGroup.getPrefWidth() - offsetX);
	}
	
	public void left() {
		// On prend le parent de l'actuel selectionne et on genere un clic
		if( selectedLabel != null && selectedLabel.getParent() != null && selectedLabel.getParent() != this ) {
			selectedLabel.getParent().click();
		}
		else if ( selectedLabel == null ) {
			selectFirst();
		}
	}
	
	public void right() {
		// On prend le parent de l'actuel selectionne et on genere un clic
		if( selectedLabel != null && selectedLabel.getParent() != null &&
			selectedLabel.children != null && selectedLabel.children.size > 0 ) {
			selectedLabel.children.get(0).click();
		}
		else if ( selectedLabel == null ) {
			selectFirst();
		}
	}
	
	public void up() {
		// On prend le parent de l'actuel selectionne et on genere un clic
		if( selectedLabel != null && selectedLabel.getParent() != null  ) {
			Array<MenuLabel> ls = selectedLabel.getParent().children;
			int index = ls.indexOf(selectedLabel, true);
			if( index > 0 ) {
				ls.get(index-1).click();
			}
		}
		else if ( selectedLabel == null ) {
			selectFirst();
		}
	}
	
	public void down() {
		// On prend le parent de l'actuel selectionne et on genere un clic
		if( selectedLabel != null && selectedLabel.getParent() != null  ) {
			Array<MenuLabel> ls = selectedLabel.getParent().children;
			int index = ls.indexOf(selectedLabel, true);
			if( index < ls.size - 1 ) {
				ls.get(index+1).click();
			}
		}
		else if ( selectedLabel == null ) {
			selectFirst();
		}
	}
	
	public void selectFirst() {
		if( children != null && children.size > 0 ) {
			children.get(0).click();
		}
	}
}
