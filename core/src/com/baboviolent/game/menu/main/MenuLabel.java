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
	protected MenuLabel parent;
	protected MenuLabelRoot root;
	protected Array<MenuLabel> children;
	protected String name;
	protected Label label;
	protected LabelContainerGroup childrenGroup;
	protected Skin skin;
	protected int nbInGroup; // Nombre d'lement dans mon groupe
	protected int positionInGroup; // Ma position dans le groupe a partir de 0
	
	public MenuLabel() {
	}
	
	public MenuLabel(String name, MenuLabel parent) {
		init(name, parent);
		root = parent.getRoot();
	}
	
	public MenuLabel(String name, MenuLabelRoot root) {
		init(name, root);
		this.root = root;
	}
	
	private void init(String name, MenuLabel parent) {
		children = new Array<MenuLabel>();
		this.name = name;
		this.parent = parent;
		parent.addChild(this);
		childrenGroup = new LabelContainerGroup();
	}
	
	protected boolean isRoot() {
		return false;
	}
	
	// Est ce que c'est une feuille
	protected boolean isEnd() {
		if(children.size == 0) {
			return true;
		}
		return false;
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
	
	public MenuLabel getParent() {
		return parent;
	}
	
	// Renvoie tous les children groups jusqu'a lui
	private Array<LabelContainerGroup> getChildrenGroups() {
		Array<LabelContainerGroup> results = new Array<LabelContainerGroup>();
		
		results.add(childrenGroup);
		MenuLabel parentIter = this.parent;
		while( parentIter != null ) {
			results.add(parentIter.childrenGroup);
			parentIter = parentIter.getParent();
		}
		return results;
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
	
	private MenuLabelRoot getRoot() {
		return root;
	}
	
	public LabelContainerGroup getChildrenGroup() {
		return childrenGroup;
	}
	
	public void compute() {
		label = new Label(name, skin);
		label.addListener( new ClickListener() { public void clicked (InputEvent event, float x, float y) {
			click();
     	}});
		
		childrenGroup.align(Align.left);
		for( int i = 0; i < children.size; i++ ) {
			MenuLabel child = children.get(i);
			child.setSkin(skin).compute();
			childrenGroup.addActor(child.getLabel());
		}
	}
	
	public void computeGroup() {
		// On va chercher dans l'ordre tous les elements de mon pere
		nbInGroup = parent.children.size;
		for( int i = 0; i < parent.children.size; i++ ) {
			if( parent.children.get(i) == this ) {
				positionInGroup = i;
			}
		}
		
		for( int i = 0; i < children.size; i++ ) {
			children.get(i).computeGroup();
		}
	}
	
	public void click() {
		int level = getLevel();
		final MenuLabel oldSelectedLabel = root.getSelectedLabel();
		
		if( oldSelectedLabel == this ) {
			return;
		}
		// Si pas d'enfants, c'est la feuille de l'arbre,
		// On le positionne en haut a gauche pour laisser de la place
		if( children.size == 0 ) {
			action5();
		}
		// Si c'est le niveau suivant
		else if( oldSelectedLabel == null || level == oldSelectedLabel.getLevel() + 1 ) {
			action1();
		}
		// Si c'est le meme niveau
		else if( level == oldSelectedLabel.getLevel() ) {
			action2();
		}
		// Si c'est le niveau precedent
		else if( level == oldSelectedLabel.getLevel() - 1 ) {
			action3();
		}
		// Si c'est le niveau deux fois precedent
		else if( level == oldSelectedLabel.getLevel() - 2 ) {
			action4();
		}
		
		// @TODO A tester le niveau trois fois precedant mais la proba est faible
		root.setSelectedLabel(this);
	}
	
	/**
	 * Action1
	 * NIVEAU SUIVANT
	 */
	private void action1() {
		label.addAction(Actions.color(new Color(1,0,0,1), MainMenu.animationTime));
		parent.childrenGroup.addAction(Actions.alpha(0.7f, MainMenu.animationTime));

		ContainerGroup container = (ContainerGroup) parent.getChildrenGroup().getParent();
		LabelContainerGroup pg = parent.getChildrenGroup();
		container.addActor(childrenGroup);
		childrenGroup.setColor(1, 1, 1, 0);
		childrenGroup.addAction(Actions.fadeIn(MainMenu.animationTime/2, Interpolation.pow2));
		float targetX = MainMenu.width - getWidthAllGroup();
		container.addAction(Actions.moveTo(targetX, container.getY(), MainMenu.animationTime, Interpolation.pow2));
		pg.addAction(Actions.moveTo(pg.getX(), getCenterY(), MainMenu.animationTime, Interpolation.pow2));
		pg.setManual(true);
	}
	
	/**
	 * Action2
	 * MENU AU MEME NIVEAU
	 */
	private void action2() {
		final MenuLabel oldSelectedLabel = root.getSelectedLabel();
		label.addAction(Actions.color(new Color(1,0,0,1), MainMenu.animationTime));
		oldSelectedLabel.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
		
		final HorizontalGroup container = (HorizontalGroup) parent.getChildrenGroup().getParent();
		
		float targetX = MainMenu.width - getWidthAllGroup();
		container.addAction(Actions.moveTo(targetX, container.getY(), MainMenu.animationTime, Interpolation.pow2));
		
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
	
	/**
	 * Action3
	 * NIVEAU PRECEDENT
	 */
	private void action3() {
		final MenuLabel oldSelectedLabel = root.getSelectedLabel();
		label.addAction(Actions.color(new Color(1,0,0,1), MainMenu.animationTime));
		oldSelectedLabel.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
		
		if( oldSelectedLabel.parent != this ) {
			oldSelectedLabel.parent.getLabel().addAction(Actions.color(new Color(1,1,1,1), MainMenu.animationTime));
		}
		
		// Si le dernier selectionne etait une feuille, on les raffiche
		if( oldSelectedLabel.isEnd() ) {
			Array<MenuLabel> a = oldSelectedLabel.parent.children;
			for( int i = 0; i < a.size; i++) {
				if( a.get(i) != this ) {
					a.get(i).label.addAction(Actions.fadeIn(MainMenu.animationTime));
				}
			}
			oldSelectedLabel.parent.getChildrenGroup().addAction(
					Actions.moveTo(
							oldSelectedLabel.parent.childrenGroup.getX(),
							MainMenu.height/2 - oldSelectedLabel.parent.childrenGroup.getPrefHeight()/2,
							MainMenu.animationTime/2, Interpolation.pow2));		}
		
		final ContainerGroup container = (ContainerGroup) parent.getChildrenGroup().getParent();
		
		// On supprime les deux sous menu precedement ouvert
		final MenuLabel me = this;
		Action completeAction = new Action(){
		    public boolean act( float delta ) {
		    	container.removeActor(oldSelectedLabel.childrenGroup);
		    	if( oldSelectedLabel.parent != me ) {
		    		container.removeActor(oldSelectedLabel.parent.childrenGroup);
		    		childrenGroup.setColor(1,1,1,0);
		    		container.addActor(childrenGroup);
		    	}
		    	childrenGroup.addAction(Actions.fadeIn(MainMenu.animationTime/2, Interpolation.pow2));
		        return true;
		    }
		};
		oldSelectedLabel.childrenGroup.addAction(Actions.sequence(Actions.fadeOut(MainMenu.animationTime/2, Interpolation.pow2),
				completeAction));
		if( oldSelectedLabel.parent != this ) {
			oldSelectedLabel.parent.childrenGroup.addAction(Actions.fadeOut(MainMenu.animationTime/2, Interpolation.pow2));
		}
		
		float targetX = MainMenu.width - getWidthAllGroup();
		container.addAction(Actions.moveTo(targetX, container.getY(), MainMenu.animationTime, Interpolation.pow2));
		
		// On aligne le menu
		LabelContainerGroup pg = parent.getChildrenGroup();
		pg.addAction(Actions.moveTo(pg.getX(), getCenterY(), MainMenu.animationTime/2, Interpolation.pow2));
	}
	
	/**
	 * Action4
	 * NIVEAU 2 FOIS PRECEDENT
	 */
	private void action4() {
		final MenuLabel oldSelectedLabel = root.getSelectedLabel();
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
		
		/*float oSize1 = oldSelectedLabel.childrenGroup.getPrefWidth();
		float oSize2 = oldSelectedLabel.parent.childrenGroup.getPrefWidth();
		float oSize3 = oldSelectedLabel.parent.parent.childrenGroup.getPrefWidth();
		float xMove = container.getX() + oSize1 + oSize2 + oSize3 + 3*container.getSpace();
		xMove = xMove - childrenGroup.getPrefWidth() - container.getSpace();*/
		float targetX = MainMenu.width - getWidthAllGroup();
		container.addAction(Actions.moveTo(targetX, container.getY(), MainMenu.animationTime, Interpolation.pow2));
		
		// On aligne le menu
		LabelContainerGroup pg = parent.getChildrenGroup();
		pg.addAction(Actions.moveTo(pg.getX(), getCenterY(), MainMenu.animationTime/2, Interpolation.pow2));
	}
	
	/**
	 * Action5
	 * DERNIER NIVEAU, FEUILLE
	 */
	private void action5() {
		label.addAction(Actions.color(new Color(1,0,0,1), MainMenu.animationTime));
		
		// On met tous les enfants du meme groupe transparent
		for( int i = 0; i < parent.children.size; i++) {
			if( parent.children.get(i) != this ) {
				parent.children.get(i).label.addAction(Actions.fadeOut(MainMenu.animationTime));
			}
		}

		ContainerGroup container = (ContainerGroup) parent.getChildrenGroup().getParent();
		LabelContainerGroup pg = parent.getChildrenGroup();
		
		float targetX = -getWidthAllGroup() + this.label.getPrefWidth()*1.7f + container.getSpace();
		container.addAction(Actions.moveTo(targetX, container.getY(), MainMenu.animationTime, Interpolation.pow2));
		pg.addAction(Actions.moveTo(pg.getX(), getTopY(), MainMenu.animationTime, Interpolation.pow2));
		pg.setManual(true);
	}
	
	private float getCenterY() {
		VerticalGroup pg = parent.getChildrenGroup();
		float totalSize = pg.getPrefHeight();
		float elemSize = totalSize/nbInGroup;
		
		return MainMenu.height/2 - totalSize + elemSize/2 + elemSize*positionInGroup;
	}
	
	private float getTopY() {
		VerticalGroup pg = parent.getChildrenGroup();
		float totalSize = pg.getPrefHeight();
		float elemSize = totalSize/nbInGroup;
		
		return MainMenu.height - totalSize + elemSize/2 + elemSize*positionInGroup - 1.2f*elemSize;
	}
	
	private float getWidthAllGroup() {
		float result = 0;
		ContainerGroup container = (ContainerGroup) parent.getChildrenGroup().getParent();
		Array<LabelContainerGroup> groups = getChildrenGroups();
		for( int i = 0; i < groups.size; i++ ) {
			result += groups.get(i).getPrefWidth();
			result += container.getSpace();
		}
		return result;
	}
}
