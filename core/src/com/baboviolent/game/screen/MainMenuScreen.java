package com.baboviolent.game.screen;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class MainMenuScreen implements Screen {
	final BaboViolentGame game;
    PerspectiveCamera camera;

    public MainMenuScreen(final BaboViolentGame g) {
        game = g;
        camera = new PerspectiveCamera();
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Drop!!! ", 100, 150);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }
    
	@Override
	public void resize(int width, int height) {
	}
	 
	@Override
	public void show() {
	}
	 
	@Override
	public void hide() {
	}
	 
	@Override
	public void pause() {
	}
	 
	@Override
	public void resume() {
	}
	 
	@Override
	public void dispose() {
	}
}
