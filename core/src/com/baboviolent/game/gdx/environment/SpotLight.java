package com.baboviolent.game.gdx.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Vector3;

public class SpotLight extends BaseLight {
	public final Vector3 position = new Vector3();
	public final Vector3 direction = new Vector3();
	public float intensity;
	public float angleCos;
	public float exponent;
	
	public SpotLight set (final SpotLight copyFrom) {
		return set(copyFrom.color, copyFrom.position,
				copyFrom.direction, copyFrom.intensity,
				copyFrom.angleCos, copyFrom.exponent);
	}
	
	public SpotLight set (
			final Color color, final Vector3 position,
			final Vector3 direction, final float intensity, 
			final float angleCos, final float exponent) {
		if (color != null) this.color.set(color);
		if (position != null) this.position.set(position);
		if (direction != null) this.direction.set(direction).nor();
		this.intensity = intensity;
		this.angleCos = angleCos;
		this.exponent = exponent;
		return this;
	}

	public SpotLight set (final float r, final float g, final float b, final Vector3 position, final Vector3 direction, final float intensity, final float angleCos) {
		this.color.set(r, g, b, 1f);
		if (position != null) this.position.set(position);
		if (direction != null) this.direction.set(direction).nor();
		this.intensity = intensity;
		this.angleCos = angleCos;
		return this;
	}

	public SpotLight set (final Color color, final float x, final float y, final float z, final float x2, final float y2, final float z2, final float intensity, final float angleCos) {
		if (color != null) this.color.set(color);
		this.position.set(x, y, z);
		this.direction.set(x2, y2, z2).nor();
		this.intensity = intensity;
		this.angleCos = angleCos;
		return this;
	}

	public SpotLight set (final float r, final float g, final float b, final float x, final float y, final float z,
						  final float x2, final float y2, final float z2, final float intensity, final float angleCos) {
		this.color.set(r, g, b, 1f);
		this.position.set(x, y, z);
		this.direction.set(x2, y2, z2).nor();
		this.intensity = intensity;
		this.angleCos = angleCos;
		return this;
	}
	
	public SpotLight setTarget(final Vector3 target) {
		direction.set(target).sub(position).nor();
		return this;
	}

	@Override
	public boolean equals (Object obj) {
		return (obj instanceof SpotLight) ? equals((SpotLight)obj) : false;
	}

	public boolean equals (SpotLight other) {
		return (other != null && (other == this || (color.equals(other.color) && position.equals(other.position) && direction.equals(other.direction) && intensity == other.intensity && angleCos == other.angleCos)));
	}
}
