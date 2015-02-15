package com.baboviolent.game.menu.extra;

import com.baboviolent.game.menu.main.MenuShader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

public class MenuBackground implements Disposable {
	private MenuShader menuShader;
    private RenderContext renderContext;
    private long startTime;
    private float maxTime = 15000;
    private boolean invert = false;
    
    public MenuBackground() {
    	startTime = TimeUtils.millis();
        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
        menuShader = new MenuShader();
        menuShader.init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    public void render() {
    	render(1f);
    }
    
    // Si le temps est superieur a 20 secondes, ca pixellise
    // Donc on fait un aller retour pour eviter ca
    public void render(float alpha) {
    	long timeSince = TimeUtils.timeSinceMillis(startTime);
    	if( timeSince >= maxTime ) {
    		startTime = TimeUtils.millis();
    		timeSince = 0;
    		if( invert )
    			invert = false;
    		else 
    			invert = true;
    	}
    	
    	float currentValue = Interpolation.pow2.apply(0f, maxTime, (float) timeSince/maxTime);
    	
    	if( invert ) {
    		currentValue = maxTime - currentValue;
    	}
    	
    	renderContext.begin();
        menuShader.begin(currentValue, alpha);
        menuShader.render();
        menuShader.end();
        renderContext.end();
    }

	@Override
	public void dispose() {
		menuShader.dispose();
	}
}
