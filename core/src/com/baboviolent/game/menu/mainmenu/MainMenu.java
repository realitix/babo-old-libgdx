package com.baboviolent.game.menu.mainmenu;

import com.baboviolent.game.hud.widget.CartridgeWidget;
import com.baboviolent.game.hud.widget.GrenadeWidget;
import com.baboviolent.game.hud.widget.LifeWidget;
import com.baboviolent.game.hud.widget.MolotovWidget;
import com.baboviolent.game.hud.widget.ReloadWidget;
import com.baboviolent.game.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainMenu {
	private Stage stage;
	private ContainerGroup container;
	private Skin skin;
	private MenuLabel root;
	
	// Taille
	public static final int width = 1920;
	public static final int height = 1080;
	
	// Animation
	public static final float animationTime = 1;
	public static final float animationMoveX = 100;
	
	// Goupes
	private VerticalGroup group1;
	private VerticalGroup group2;
	
	// Labels
	private Label label11;
	private Label label12;
	private Label label1121;
	private Label label1122;
	
	// Menu selectionne
	private int selected;
	
	public MainMenu() {
 		stage = new Stage(new FillViewport(width, height));
 		Gdx.input.setInputProcessor(stage);
 		
 		container = new ContainerGroup();
 		container.space(100);
 		
 		container.setFillParent(true);
 		container.setPosition(0, 0);
 		stage.addActor(container);
 		skin = new Skin(Gdx.files.internal("data/skin/main_menu/skin.json"));
 		
 		initMenuLabel();
 		startMenu();
 		
 		//container.debugAll();
	}
	
	private void initMenuLabel() {
		root = new MenuLabel(skin);
		MenuLabel labelSolo = new MenuLabel("Solo", root);
		MenuLabel labelMulti = new MenuLabel("Multi", root);
		MenuLabel labelOptions = new MenuLabel("Options", root);
		
		MenuLabel labelTraining = new MenuLabel("Entrainement", labelSolo);
		MenuLabel labelLevels = new MenuLabel("Niveaux", labelSolo);
		
		MenuLabel labelOnline = new MenuLabel("En ligne", labelMulti);
		MenuLabel labelLocal = new MenuLabel("En local", labelMulti);
		
		MenuLabel lvl1 = new MenuLabel("Niveau 1", labelLevels);
		MenuLabel lvl2 = new MenuLabel("Niveau 2", labelLevels);
		MenuLabel lvl3 = new MenuLabel("Niveau 3", labelLevels);
		MenuLabel lvl4 = new MenuLabel("Niveau 4", labelLevels);
		MenuLabel lvl5 = new MenuLabel("Niveau 5", labelLevels);
	}
	
	private void startMenu() {
		root.compute();
		root.computeGroup();
		container.addActor(root.getChildrenGroup());
		root.computeInitX();
	}
	
	/**
	 * GROUPE 1
	 */	
	private void addGroup1() {
		group1 = new VerticalGroup();
		group1.align(Align.left);
		
		label11 = new Label("Solo", skin);
		label11.addListener(	new ClickListener() { public void clicked (InputEvent event, float x, float y) {
			toGroup112(); selected = 11; 
     	}});
		
		label12 = new Label("Multi", skin);
		label12.addListener(	new ClickListener() { public void clicked (InputEvent event, float x, float y) {
			toGroup122(); selected = 12; 
     	}});
		
		group1.addActor(label11);
		group1.addActor(label12);
		
		container.addActor(group1);
	}
	
	private void toGroup112() {
		// Rien etait selectionne
		if(selected == 0) {
			// On modifie le label clique
			label11.addAction(Actions.color(new Color(1,0,0,1), animationTime));
			
			// On diminue l'alpha du groupe 1
			group1.addAction(Actions.alpha(0.7f, animationTime));
			
			// On affiche 
			// On centre le menu selectionne
			addGroup112();
			container.addAction(Actions.moveTo(container.getX() - animationMoveX, container.getY(), animationTime, Interpolation.pow2));
			group1.addAction(Actions.moveTo(group1.getX(), group1.getY() - group1.getHeight()/4, animationTime, Interpolation.pow2));
		}
		// On est deja sur le groupe 2 mais pas le bon
		if( selected == 12 ) {
			label11.addAction(Actions.color(new Color(1,0,0,1), animationTime));
			label12.addAction(Actions.color(new Color(1,1,1,1), animationTime));
			group1.addAction(Actions.moveTo(group1.getX(), group1.getY() - group1.getHeight()/2, animationTime, Interpolation.pow2));
		}
	}
	
	private void toGroup122() {
		// On modifie le label clique
		label12.addAction(Actions.color(new Color(1,0,0,1), animationTime));
		
		// On diminue l'alpha du groupe 1
		group1.addAction(Actions.alpha(0.7f, animationTime));
		
		// On affiche 
		// On centre le menu selectionne
		addGroup122();
		container.addAction(Actions.moveTo(container.getX() - animationMoveX, container.getY(), animationTime, Interpolation.pow2));
		group1.addAction(Actions.moveTo(group1.getX(), group1.getY() + group1.getHeight()/4, animationTime, Interpolation.pow2));
	}
	
	/**
	 * GROUPE 2
	 */	
	private void addGroup112() {
		group2 = new VerticalGroup();
		
		label1121 = new Label("Entrainement", skin);
		group2.addActor(label1121);
		
		Label label1122 = new Label("Niveaux", skin);
		group2.addActor(label1122);
		
		container.addActor(group2);
	}
	
	private void addGroup122() {
		VerticalGroup group = new VerticalGroup();
		
		Label label1 = new Label("En ligne", skin);
		group.addActor(label1);
		
		Label label2 = new Label("En local", skin);
		group.addActor(label2);
		
		container.addActor(group);
	}
	
	public void update() {
		stage.act(Gdx.graphics.getDeltaTime());        
	}
	
	public void render() {
		stage.draw();
	}
}
