package com.baboviolent.game.effect.light.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class BaboLightEffect {

	protected boolean active;
	protected PointLight light;
	protected int life; // Temps de vie en milliseconde
	protected long startTime;
	protected Vector3 position;
	protected Color color;
	protected float intensity;
	protected String name;
	
	public BaboLightEffect() {
		position = new Vector3();
		color = new Color();
		light = new PointLight();
	}
	
	public BaboLightEffect(BaboLightEffect effect) {
		color = new Color(effect.getColor());
		name = effect.getName();
		life = effect.getLife();
		intensity = effect.getIntensity();
		position = effect.getPosition().cpy();
		light = new PointLight().set(effect.getLight());
	}
	
	public void reset() {
		active = true;
		startTime = 0;
	}
	
	public void init() {
		active = true;
		light.set(color, position, intensity);
	}
	
	public void start() {
		startTime = TimeUtils.millis();
	}
	
	public BaboLightEffect copy() {
		return new BaboLightEffect(this);
	}
	
	public boolean isActive() {
		return active;
	}
	
	public PointLight getLight() {
		return light;
	}
	
	public void setTransform(Matrix4 transform) {
		transform.getTranslation(position);
	}
	
	public Vector3 getPosition() {
		return position;
	}
	
	public void update() {
		if( TimeUtils.timeSinceMillis(startTime) > life ) {
			active = false;
		}
	}
	
	public int getLife() {
		return life;
	}

	public Color getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

	public String getName() {
		return name;
	}
}
