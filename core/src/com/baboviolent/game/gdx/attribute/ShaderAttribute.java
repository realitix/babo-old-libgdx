package com.baboviolent.game.gdx.attribute;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.NumberUtils;

public class ShaderAttribute extends Attribute {
	public final static int DEFAULT = 1;
	public final static int BABO = 2;
	
	public final static String Alias = "shader";
	public final static long Type = register(Alias);
	
	public int shader;
	
	public final static boolean is (final long mask) {
		return (mask & Type) == mask;
	}
	
	protected ShaderAttribute(long type) {
		super(type);
	}

	public ShaderAttribute (final int shader) {
		super(Type);
		this.shader = shader;
	}

	public ShaderAttribute (final ShaderAttribute copyFrom) {
		this(copyFrom.shader);
	}

	@Override
	public ShaderAttribute copy () {
		return new ShaderAttribute(this);
	}
	
	@Override
    public int hashCode () {
        final int prime = 1069;
        final long v = NumberUtils.doubleToLongBits(shader);
        return prime * super.hashCode() + (int)(v^(v>>>32));
    }
}