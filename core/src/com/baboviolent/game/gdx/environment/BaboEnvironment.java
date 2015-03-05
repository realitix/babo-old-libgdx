package com.baboviolent.game.gdx.environment;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class BaboEnvironment extends Environment {
	public final Array<SpotLight> spotLights = new Array<SpotLight>();
	
	@Override
	public BaboEnvironment add (BaseLight light) {
		if (light instanceof DirectionalLight)
			directionalLights.add((DirectionalLight)light);
		else if (light instanceof PointLight)
			pointLights.add((PointLight)light);
		else if (light instanceof SpotLight)
			spotLights.add((SpotLight)light);
		else
			throw new GdxRuntimeException("Unknown light type");
		return this;
	}
	
	@Override
	public BaboEnvironment remove (BaseLight light) {
		if (light instanceof DirectionalLight)
			directionalLights.removeValue((DirectionalLight)light, false);
		else if (light instanceof PointLight)
			pointLights.removeValue((PointLight)light, false);
		else if (light instanceof SpotLight)
			spotLights.removeValue((SpotLight)light, false);
		else
			throw new GdxRuntimeException("Unknown light type");
		return this;
	}
	
	@Override
	public void clear () {
		super.clear();
		spotLights.clear();
	}
}
