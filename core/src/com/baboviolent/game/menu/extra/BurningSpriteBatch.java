package com.baboviolent.game.menu.extra;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;

public class BurningSpriteBatch extends SpriteBatch {
	private ShaderProgram shaderProgram;
	private int u_time;
	private int u_resolution;
	private long startTime;
	private float width;
	private float height;
	
	public BurningSpriteBatch() {
		super();
		String p = BaboViolentGame.PATH_SHADERS;
        String vert = Gdx.files.internal(p+"/burningText.vertex.glsl").readString();
        String frag = Gdx.files.internal(p+"/burningText.fragment.glsl").readString();
        shaderProgram = new ShaderProgram(vert, frag);
        if (!shaderProgram.isCompiled())
            throw new GdxRuntimeException(shaderProgram.getLog());
        
        u_time = shaderProgram.getUniformLocation("u_time");
        u_resolution = shaderProgram.getUniformLocation("u_resolution");
        this.setShader(shaderProgram);
        
        startTime = TimeUtils.millis();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
	}
	
	@Override
	public void begin () {
		super.begin();
		long time = TimeUtils.timeSinceMillis(startTime);
		shaderProgram.setUniformf(u_time, time/1000f);
		shaderProgram.setUniformf(u_resolution, width, height);
	}
}
