package com.baboviolent.game.hud;

import com.baboviolent.game.gdx.batch.BaboSpriteBatch;
import com.baboviolent.game.hud.widget.CartridgeWidget;
import com.baboviolent.game.hud.widget.GrenadeWidget;
import com.baboviolent.game.hud.widget.LifeWidget;
import com.baboviolent.game.hud.widget.MolotovWidget;
import com.baboviolent.game.hud.widget.ReloadWidget;
import com.baboviolent.game.loader.BaboAssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class Hud {
	private Stage stage;
	private LifeWidget lifeWidget;
	private CartridgeWidget cartridgeWidget;
	private GrenadeWidget grenadeWidget;
	private MolotovWidget molotovWidget;
	private ReloadWidget reloadWidget;
	
	public Hud(BaboSpriteBatch spriteBatch) {
		int width = 1920;
 		int height = 1080;
 		stage = new Stage(new FillViewport(width, height), spriteBatch);
 		
 		Skin skin = BaboAssetManager.getSkin("hud");
 		
 		// Barre de vie
 		lifeWidget = new LifeWidget(skin);
 		lifeWidget.setBounds(735, 50, 450, 40);
 		lifeWidget.setValue(100);
 		stage.addActor(lifeWidget);
 		
 		// Cartouches
 		cartridgeWidget = new CartridgeWidget("0", skin);
 		cartridgeWidget.setBounds(790, 100, 64, 64);
 		stage.addActor(cartridgeWidget);
 		
 		// Grenades
 		grenadeWidget = new GrenadeWidget("0", skin);
 		grenadeWidget.setBounds(940, 100, 64, 64);
 		stage.addActor(grenadeWidget);
 		
 		// Molotovs
 		molotovWidget = new MolotovWidget("0", skin);
 		molotovWidget.setBounds(1090, 100, 64, 64);
 		stage.addActor(molotovWidget);
 		
 		// Barre de recharge
 		reloadWidget = new ReloadWidget(skin);
 		reloadWidget.setBounds(810, 200, 300, 30);
 		stage.addActor(reloadWidget);
	}
	
	public void setLife(float life) {
		lifeWidget.setValue(life);
	}
	
	public void setCartridge(int nb) {
		cartridgeWidget.setText(Integer.toString(nb));
	}
	
	public void setGrenade(int nb) {
		grenadeWidget.setText(Integer.toString(nb));
	}
	
	public void setMolotov(int nb) {
		molotovWidget.setText(Integer.toString(nb));
	}
	
	public void reload(int time) {
		reloadWidget.start(time);
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void update() {
		stage.act(Gdx.graphics.getDeltaTime());        
	}
	
	public void render() {
		stage.draw();
	}
}
