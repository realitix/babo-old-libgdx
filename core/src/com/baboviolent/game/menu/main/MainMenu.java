package com.baboviolent.game.menu.main;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.hud.widget.CartridgeWidget;
import com.baboviolent.game.hud.widget.GrenadeWidget;
import com.baboviolent.game.hud.widget.LifeWidget;
import com.baboviolent.game.hud.widget.MolotovWidget;
import com.baboviolent.game.hud.widget.ReloadWidget;
import com.baboviolent.game.menu.extra.AnimatedText;
import com.baboviolent.game.menu.extra.BurningSpriteBatch;
import com.baboviolent.game.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
import com.badlogic.gdx.utils.GdxRuntimeException;
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
 		stage = new Stage(new FillViewport(width, height), new BurningSpriteBatch());
 		Gdx.input.setInputProcessor(stage);
 		
 		container = new ContainerGroup();
 		container.space(100);
 		
 		container.setFillParent(true);
 		container.setPosition(0, 0);
 		stage.addActor(container);
 		skin = new Skin(Gdx.files.internal("data/skin/main_menu/skin.json"));
 		
 		initMenuLabel();
 		startMenu();
 		
 		// Ajout du titre
 		Skin skinTitle = new Skin(Gdx.files.internal("data/skin/main_menu/skin_title.json"));
 		AnimatedText title = new AnimatedText("Babo Violent 2", skinTitle);
 		title.setPosition(200, height - title.getPrefHeight()/3f);
 		stage.addActor(title);
 		
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
	
	public void update() {
		stage.act(Gdx.graphics.getDeltaTime());        
	}
	
	public void render() {
		stage.draw();
	}
}
