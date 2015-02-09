package com.baboviolent.game.menu.extra;

import com.baboviolent.game.shader.MenuShader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.TimeUtils;

public class MenuBackground {
	private MenuShader menuShader;
    private RenderContext renderContext;
    private long startTime;
    
    public MenuBackground() {
    	startTime = TimeUtils.millis();
        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
        menuShader = new MenuShader();
        menuShader.init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    public void render() {
    	renderContext.begin();
        menuShader.begin(TimeUtils.timeSinceMillis(startTime));
        menuShader.render();
        menuShader.end();
        renderContext.end();
    }
}
