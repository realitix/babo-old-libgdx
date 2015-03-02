package com.baboviolent.game.menu.loading;

import com.baboviolent.game.menu.extra.AnimatedText;
import com.baboviolent.game.menu.main.MainMenu;
import com.baboviolent.game.menu.main.input.MenuGestureListener;
import com.baboviolent.game.menu.main.input.MenuInputListener;
import com.baboviolent.game.menu.main.submenu.SubMenu;
import com.baboviolent.game.menu.main.ui.ContainerGroup;
import com.baboviolent.game.menu.main.ui.MenuStage;
import com.baboviolent.game.screen.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class LoadingMenu implements Disposable {
	private Stage stageLoading;
	private Loading loading;
	
	public static final int width = 1920;
	public static final int height = 1080;
	
	public LoadingMenu() {		
 		stageLoading = new Stage(new FillViewport(width, height));
		loading = new Loading();
		loading.getColor().a = 0;
		stageLoading.addActor(loading);
		loading.addAction(Actions.fadeIn(MainMenu.animationTime/2, Interpolation.pow2));
	}
	
	public LoadingMenu setPercent(int p) {
		loading.setPercent(p);
		return this;
	}
	
	public LoadingMenu update() {
		stageLoading.act(Gdx.graphics.getDeltaTime());
		return this;
	}
	
	public LoadingMenu render() {
		stageLoading.draw();
		return this;
	}

	@Override
	public void dispose() {
		loading.dispose();
		stageLoading.dispose();
	}
}
