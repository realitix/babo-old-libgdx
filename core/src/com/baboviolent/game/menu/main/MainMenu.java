package com.baboviolent.game.menu.main;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.hud.widget.CartridgeWidget;
import com.baboviolent.game.hud.widget.GrenadeWidget;
import com.baboviolent.game.hud.widget.LifeWidget;
import com.baboviolent.game.hud.widget.MolotovWidget;
import com.baboviolent.game.hud.widget.ReloadWidget;
import com.baboviolent.game.menu.extra.AnimatedText;
import com.baboviolent.game.menu.extra.BurningSpriteBatch;
import com.baboviolent.game.menu.main.input.MenuGestureListener;
import com.baboviolent.game.menu.main.input.MenuInputListener;
import com.baboviolent.game.menu.main.submenu.Loading;
import com.baboviolent.game.menu.main.submenu.SubMenu;
import com.baboviolent.game.menu.main.submenu.solo.FastGame;
import com.baboviolent.game.menu.main.tree.MenuLabel;
import com.baboviolent.game.menu.main.tree.MenuLabelRoot;
import com.baboviolent.game.menu.main.ui.ContainerGroup;
import com.baboviolent.game.menu.main.ui.MenuStage;
import com.baboviolent.game.screen.GameScreen;
import com.baboviolent.game.screen.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainMenu implements Disposable {
	private Stage stage;
	private Stage stageLoading;
	private Loading loading;
	private ContainerGroup container;
	private Skin skin;
	private MenuLabelRoot root;
	private MainMenuScreen menuScreen;
	
	// Taille
	public static final int width = 1920;
	public static final int height = 1080;
	
	// Animation
	public static final float animationTime = 1;
	public static final float animationMoveX = 100;
	
	public MainMenu(MainMenuScreen menuScreen) {
		this.menuScreen = menuScreen;
		
 		//stage = new Stage(new FillViewport(width, height), new BurningSpriteBatch());
 		stage = new MenuStage(new FillViewport(width, height), this);
 		stageLoading = new Stage(new FillViewport(width, height));
 		Gdx.input.setInputProcessor(new InputMultiplexer(
 				stage,
 				new MenuInputListener(this),
 				new GestureDetector(new MenuGestureListener(this))
 				));
 		Gdx.input.setCatchBackKey(true);
 		
 		
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
 		root.setTitle(title);
 		
 		//container.debugAll();
	}
	
	private void initMenuLabel() {
		root = new MenuLabelRoot(skin);
		MenuLabel labelSolo = new MenuLabel("Solo", root);
		MenuLabel labelMulti = new MenuLabel("Multijoueur", root);
		MenuLabel labelOptions = new MenuLabel("Options", root);
		
		// Solo
		new MenuLabel("Partie rapide", labelSolo, new FastGame(this));
		MenuLabel labelLevels = new MenuLabel("Niveaux", labelSolo);
			new MenuLabel("Niveau 1", labelLevels);
			new MenuLabel("Niveau 2", labelLevels);
			new MenuLabel("Niveau 3", labelLevels);
			new MenuLabel("Niveau 4", labelLevels);
			new MenuLabel("Niveau 5", labelLevels);
		new MenuLabel("Personnalisé", labelSolo);
		new MenuLabel("Editeur", labelSolo);
		
		// Multi
		MenuLabel labelOnline = new MenuLabel("En ligne", labelMulti);
			new MenuLabel("Partie rapide", labelOnline);
			new MenuLabel("Duel", labelOnline);
			new MenuLabel("Massacre", labelOnline);
		MenuLabel labelLocal = new MenuLabel("En local", labelMulti);
			new MenuLabel("Créer une partie", labelLocal);
			new MenuLabel("Rejoindre une partie", labelLocal);
		
		// Options
		new MenuLabel("Mon Babo", labelOptions);
		new MenuLabel("Son", labelOptions);
		new MenuLabel("Affichage", labelOptions);
		new MenuLabel("Contrôle", labelOptions);
	}
	
	private void startMenu() {
		root.compute();
		root.computeGroup();
		container.addActor(root.getChildrenGroup());
		root.computeInitX();
	}
	
	public void update() {
		stage.act(Gdx.graphics.getDeltaTime());
		stageLoading.act(Gdx.graphics.getDeltaTime());
	}
	
	public void render() {
		stage.draw();
		stageLoading.draw();
	}
	
	public void left() {
		root.left();
	}
	
	public void right() {
		root.right();
	}
	
	public void up() {
		root.up();
	}
	
	public void down() {
		root.down();
	}
	
	// Lance l'image de chargement
	public void loadingForAction(final SubMenu submenu, final int action) {
		Action completeAction = new Action(){
		    public boolean act( float delta ) {
		    	loadingAndStart(submenu, action);
		    	return true;
		    }
		};
		stage.addAction(Actions.sequence(
						Actions.fadeOut(
								MainMenu.animationTime, Interpolation.pow2),
								completeAction)
						);
	}
	
	private void loadingAndStart(final SubMenu submenu, final int action) {
		Action completeAction = new Action(){
		    public boolean act( float delta ) {
		    	submenu.startAction(action);
		    	return true;
		    }
		};
		// On affiche le chargement
		loading = new Loading();
		loading.getColor().a = 0;
		stageLoading.addActor(loading);
		loading.addAction(Actions.sequence(
				Actions.fadeIn(
						MainMenu.animationTime/2, Interpolation.pow2),
						completeAction)
				);
	}
	
	public MainMenuScreen getMenuScreen() {
		return menuScreen;
	}

	@Override
	public void dispose() {
		stage.dispose();
		stageLoading.dispose();
		skin.dispose();
		root.dispose();
		loading.dispose();
	}
}
