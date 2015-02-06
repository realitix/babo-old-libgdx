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

public class MenuLabel {
	private MenuLabel parent;
	private Array<MenuLabel> children;
	private String name;
	private Label label;
	private boolean root;
	private LabelContainerGroup childrenGroup;
	private Skin skin;
	private boolean selected;
	private MenuLabel selectedLabel;
	private int nbInGroup; // Nombre d'lement dans mon groupe
	private int positionInGroup; // Ma position dans le groupe a partir de 0
	
	// Constructeur du noeud root
	public MenuLabel(Skin skin) {
		children = new Array<MenuLabel>();
		childrenGroup = new LabelContainerGroup();
		root = true;
		this.skin = skin;
	}
	
	public MenuLabel(String name, MenuLabel parent) {
		children = new Array<MenuLabel>();
		this.name = name;
		this.parent = parent;
		root =  false;
		parent.addChild(this);
		childrenGroup = new LabelContainerGroup();
	}
	
	public MenuLabel addChild(MenuLabel child) {
		children.add(child);
		return this;
	}
	
	public MenuLabel setSkin(Skin skin) {
		this.skin = skin;
		return this;
	}
	
	public Label getLabel() {
		return label;
	}
	
	// Niveau dans l'arbre
	public int getLevel() {
		return getLevel(0);
	}
	
	public int getLevel(int l) {
		if( parent != null ) {
			l++;
			l = parent.getLevel(l);
		}
		return l;
	}
	
	private MenuLabel getRoot() {
		if(parent == null) {
			return this;
		}
		return parent.getRoot();
	}
	
	public LabelContainerGroup getChildrenGroup() {
		return childrenGroup;
	}
	
	// Dedie a root
	public void setSelectedLabel(MenuLabel l) {
		if( parent == null )
			this.selectedLabel = l;
		else
			parent.setSelectedLabel(l);
	}
	
	// Dedie a root
	public MenuLabel getSelectedLabel() {
		if( parent == null )
			return selectedLabel;
		return parent.getSelectedLabel();
	}
	
	public void compute() {
		if( !root ) {
			label = new Label(name, skin);
			label.addListener( new ClickListener() { public void clicked (InputEvent event, float x, float y) {
				click();
	     	}});
		}
		
		childrenGroup.align(Align.left);
		for( int i = 0; i < children.size; i++ ) {
			MenuLabel child = children.get(i);
			child.setSkin(skin).compute();
			childrenGroup.addActor(child.getLabel());
		}
	}
	
	public void computeGroup() {
		if( !root ) {
			// On va chercher dans l'ordre tous les elements de mon pere
			nbInGroup = parent.children.size;
			for( int i = 0; i < parent.children.size; i++ ) {
				if( parent.children.get(i) == this ) {
					positionInGroup = i;
				}
			}
		}
		
		for( int i = 0; i < children.size; i++ ) {
			children.get(i).computeGroup();
		}
	}
	
	// Root positionne le premier groupe au demarrage
	public void computeInitX() {
		ContainerGroup container = (ContainerGroup) childrenGroup.getParent();
		// @TODO a definir en fonction du ratio
		float offsetX = 250;
		container.setX(MainMenu.width - childrenGroup.getPrefWidth() - offsetX);
	}
	
	private void click() {
		int level = getLevel();
		final MenuLabel oldSelectedLabel = getSelectedLabel();
		
		if( oldSelectedLabel == this ) {
			return;
		}
		
		// Si c'est le niveau suivant
		if( oldSelectedLabel == null || level == oldSelectedLabel.getLevel() + 1 ) {
			label.addAction(Actions.color(new Color(1,0,0,1), MainMenu.animationTime));
			parent.childrenGroup.addAction(Actions.alpha(0.7f, MainMenu.animationTime));

			ContainerGroup container = (ContainerGroup) parent.getChildrenGroup().getParent();
			LabelContainerGroup pg = parent.getChildrenGroup();
			container.addActor(childrenGroup);
			childrenGroup.setColor(1, 1, 1, 1);
			float xMove = container.getX() - childrenGroup.getPrefWidth() - container.getSpace();
			container.addAction(Actions.moveTo(xMove, container.getY(), MainMenu.animationTime, Interpolation.pow2));
			pg.addAction(Actions.moveTo(pg.getX(), getCenterY(), MainMenu.animationTime, Interpolation.pow2));
			pg.setManual(true);
		}
		
		// Si c'est le meme niveau
		else if( level == oldSelectedLabel.getLevel() ) {
			label.addAction(Actions.color(new Color(1,0,0,1), MainMenu.animationTime));
			oldSelectedLabel.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
			
			final HorizontalGroup container = (HorizontalGroup) parent.getChildrenGroup().getParent();
			
			// On deplace container de la difference entre l'ancien et le nouveau bloc
			float diffSize = oldSelectedLabel.getChildrenGroup().getPrefWidth() -
					childrenGroup.getPrefWidth();
			float xMove = container.getX() + diffSize;
			container.addAction(Actions.moveTo(xMove, container.getY(), MainMenu.animationTime, Interpolation.pow2));
			
			// On fait disparaitre le menu actuel
			Action completeAction = new Action(){
			    public boolean act( float delta ) {
			    	container.removeActor(oldSelectedLabel.childrenGroup);
			    	childrenGroup.setColor(1,1,1,0);
			    	container.addActor(childrenGroup);
			    	childrenGroup.addAction(Actions.fadeIn(MainMenu.animationTime/2, Interpolation.pow2));
			        return true;
			    }
			};
			
			oldSelectedLabel.childrenGroup.addAction(Actions.sequence(Actions.fadeOut(MainMenu.animationTime/2, Interpolation.pow2),
					completeAction));

			VerticalGroup pg = parent.getChildrenGroup();
			pg.addAction(Actions.moveTo(pg.getX(), getCenterY(), MainMenu.animationTime/2, Interpolation.pow2));
		}
		
		// Si c'est le niveau precedent
		else if( level == oldSelectedLabel.getLevel() - 1 ) {
			label.addAction(Actions.color(new Color(1,0,0,1), MainMenu.animationTime));
			oldSelectedLabel.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
			oldSelectedLabel.parent.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
			final ContainerGroup container = (ContainerGroup) parent.getChildrenGroup().getParent();
			
			// On supprime les deux sous menu precedement ouvert
			Action completeAction = new Action(){
			    public boolean act( float delta ) {
			    	container.removeActor(oldSelectedLabel.childrenGroup);
			    	container.removeActor(oldSelectedLabel.parent.childrenGroup);
			    	childrenGroup.setColor(1,1,1,0);
			    	container.addActor(childrenGroup);
			    	childrenGroup.addAction(Actions.fadeIn(MainMenu.animationTime/2, Interpolation.pow2));
			        return true;
			    }
			};
			oldSelectedLabel.childrenGroup.addAction(Actions.sequence(Actions.fadeOut(MainMenu.animationTime/2, Interpolation.pow2),
					completeAction));
			oldSelectedLabel.parent.childrenGroup.addAction(Actions.fadeOut(MainMenu.animationTime/2, Interpolation.pow2));
			
			float oSize1 = oldSelectedLabel.childrenGroup.getPrefWidth();
			float oSize2 = oldSelectedLabel.parent.childrenGroup.getPrefWidth();
			float xMove = container.getX() + oSize1 + oSize2 + 2*container.getSpace();
			xMove = xMove - childrenGroup.getPrefWidth() - container.getSpace();
			container.addAction(Actions.moveTo(xMove, container.getY(), MainMenu.animationTime, Interpolation.pow2));
			
			// On aligne le menu
			LabelContainerGroup pg = parent.getChildrenGroup();
			pg.addAction(Actions.moveTo(pg.getX(), getCenterY(), MainMenu.animationTime/2, Interpolation.pow2));
		}
		
		// Si c'est le niveau deux fois precedent
		else if( level == oldSelectedLabel.getLevel() - 2 ) {
			label.addAction(Actions.color(new Color(1,0,0,1), MainMenu.animationTime));
			oldSelectedLabel.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
			oldSelectedLabel.parent.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
			oldSelectedLabel.parent.parent.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
			final ContainerGroup container = (ContainerGroup) parent.getChildrenGroup().getParent();
			
			// On supprime les trois sous menu precedement ouvert
			Action completeAction = new Action(){
			    public boolean act( float delta ) {
			    	container.removeActor(oldSelectedLabel.childrenGroup);
			    	container.removeActor(oldSelectedLabel.parent.childrenGroup);
			    	container.removeActor(oldSelectedLabel.parent.parent.childrenGroup);
			    	childrenGroup.setColor(1,1,1,0);
			    	container.addActor(childrenGroup);
			    	childrenGroup.addAction(Actions.fadeIn(MainMenu.animationTime/2, Interpolation.pow2));
			        return true;
			    }
			};
			oldSelectedLabel.childrenGroup.addAction(Actions.sequence(Actions.fadeOut(MainMenu.animationTime/2, Interpolation.pow2),
					completeAction));
			oldSelectedLabel.parent.childrenGroup.addAction(Actions.fadeOut(MainMenu.animationTime/2, Interpolation.pow2));
			oldSelectedLabel.parent.parent.childrenGroup.addAction(Actions.fadeOut(MainMenu.animationTime/2, Interpolation.pow2));
			
			float oSize1 = oldSelectedLabel.childrenGroup.getPrefWidth();
			float oSize2 = oldSelectedLabel.parent.childrenGroup.getPrefWidth();
			float oSize3 = oldSelectedLabel.parent.parent.childrenGroup.getPrefWidth();
			float xMove = container.getX() + oSize1 + oSize2 + oSize3 + 3*container.getSpace();
			xMove = xMove - childrenGroup.getPrefWidth() - container.getSpace();
			container.addAction(Actions.moveTo(xMove, container.getY(), MainMenu.animationTime, Interpolation.pow2));
			
			// On aligne le menu
			LabelContainerGroup pg = parent.getChildrenGroup();
			pg.addAction(Actions.moveTo(pg.getX(), getCenterY(), MainMenu.animationTime/2, Interpolation.pow2));
		}
		
		// @TODO A tester le niveau trois fois precedant mais la proba est faible
		
		setSelectedLabel(this);
	}
	
	private float getCenterY() {
		VerticalGroup pg = parent.getChildrenGroup();
		float totalSize = pg.getPrefHeight();
		float elemSize = totalSize/nbInGroup;
		
		return MainMenu.height/2 - totalSize + elemSize/2 + elemSize*positionInGroup;
	}
}
