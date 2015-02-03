package com.baboviolent.game.hud.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class ReloadWidget extends Widget implements Disableable {
	private ReloadWidgetStyle style;
	private float min = 0, max = 100, stepSize = 1;
	private float value, animateFromValue;
	float position;
	private float animateDuration = 1, animateTime;
	private Interpolation animateInterpolation = Interpolation.linear;
	private float[] snapValues;
	private float threshold;
	private boolean disabled;
	
	public ReloadWidget (Skin skin) {
		this(skin.get("default", ReloadWidgetStyle.class));
	}

	public ReloadWidget (Skin skin, String styleName) {
		this(skin.get(styleName, ReloadWidgetStyle.class));
	}

	/** Creates a new progress bar. It's width is determined by the given prefWidth parameter, its height is determined by the
	 * maximum of the height of either the progress bar {@link NinePatch} or progress bar handle {@link TextureRegion}. The min and
	 * max values determine the range the values of this progress bar can take on, the stepSize parameter specifies the distance
	 * between individual values.
	 * <p>
	 * E.g. min could be 4, max could be 10 and stepSize could be 0.2, giving you a total of 30 values, 4.0 4.2, 4.4 and so on.
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param stepSize the step size between values
	 * @param style the {@link ProgressBarStyle} */
	public ReloadWidget (ReloadWidgetStyle style) {
		setStyle(style);
		this.value = min;
		disabled = true;
		setSize(getPrefWidth(), getPrefHeight());
	}

	public void setStyle (ReloadWidgetStyle style) {
		if (style == null) throw new IllegalArgumentException("style cannot be null.");
		this.style = style;
		invalidateHierarchy();
	}

	/** Returns the progress bar's style. Modifying the returned style may not have an effect until
	 * {@link #setStyle(ProgressBarStyle)} is called. */
	public ReloadWidgetStyle getStyle () {
		return style;
	}
	
	public void start(int timeMilli) {
		animateDuration = (float)timeMilli / 1000f;
		value = max;
		animateFromValue = min;
		animateTime = animateDuration;
		disabled = false;
	}

	@Override
	public void act (float delta) {
		if( disabled )
			return;
		
		super.act(delta);
		if (animateTime > 0) {
			animateTime -= delta;
			Stage stage = getStage();
			if (stage != null && stage.getActionsRequestRendering()) Gdx.graphics.requestRendering();
		}
		else {
			disabled = true;
		}
	}

	/**
	 * On dessine dans l'ordre,
	 * le blanc, le noir et ensuite le vert
	 */
	@Override
	public void draw (Batch batch, float parentAlpha) {
		if( disabled )
			return;
		
		ReloadWidgetStyle style = this.style;
		final Drawable pixel = style.pixel;
		
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
		float value = getVisualValue();

		// Blanc
		batch.setColor(1, 1, 1, parentAlpha);
		pixel.draw(batch, x, y, width, height);
		
		// Noir - petit espace
		batch.setColor(0, 0, 0, parentAlpha);
		pixel.draw(batch, x+2, y+2, width-6, height-3);
		
		// Vert - espace un peu pus grand
		// Faible -> rouge, eleve -> vert
		float red = animateInterpolation.apply(min, max, 1f - (value+min)/max)/max;
		float green = 1f - red;
		width = (width - 12) * (value/max);
		batch.setColor(red, green, 0, parentAlpha);
		pixel.draw(batch, x+6, y+6, width, height-11);
	}

	public float getValue () {
		return value;
	}

	/** If {@link #setAnimateDuration(float) animating} the progress bar value, this returns the value current displayed. */
	public float getVisualValue () {
		if (animateTime > 0) return animateInterpolation.apply(animateFromValue, value, 1f - animateTime / animateDuration);
		return value;
	}

	/** Sets the progress bar position, rounded to the nearest step size and clamped to the minimum and maximum values.
	 * {@link #clamp(float)} can be overridden to allow values outside of the progress bar's min/max range.
	 * @return false if the value was not changed because the progress bar already had the value or it was canceled by a listener. */
	public boolean setValue (float value) {
		value = clamp(Math.round(value / stepSize) * stepSize);
		float oldValue = this.value;
		if (value == oldValue) return false;
		float oldVisualValue = getVisualValue();
		this.value = value;
		if (animateDuration > 0) {
			animateFromValue = oldVisualValue;
			animateTime = animateDuration;
		}
		return true;
	}

	/** Clamps the value to the progress bar's min/max range. This can be overridden to allow a range different from the progress
	 * bar knob's range. */
	protected float clamp (float value) {
		return MathUtils.clamp(value, min, max);
	}

	/** Sets the range of this progress bar. The progress bar's current value is clamped to the range. */
	public void setRange (float min, float max) {
		if (min > max) throw new IllegalArgumentException("min must be <= max");
		this.min = min;
		this.max = max;
		if (value < min)
			setValue(min);
		else if (value > max) setValue(max);
	}

	public void setStepSize (float stepSize) {
		if (stepSize <= 0) throw new IllegalArgumentException("steps must be > 0: " + stepSize);
		this.stepSize = stepSize;
	}

	public float getPrefWidth () {
		final Drawable bg = style.pixel;
		return bg.getMinWidth();
		
	}

	public float getPrefHeight () {
		final Drawable bg = style.pixel;
		return bg.getMinHeight();
	}

	public float getMinValue () {
		return this.min;
	}

	public float getMaxValue () {
		return this.max;
	}

	public float getStepSize () {
		return this.stepSize;
	}

	/** If > 0, changes to the progress bar value via {@link #setValue(float)} will happen over this duration in seconds. */
	public void setAnimateDuration (float duration) {
		this.animateDuration = duration;
	}

	/** Sets the interpolation to use for {@link #setAnimateDuration(float)}. */
	public void setAnimateInterpolation (Interpolation animateInterpolation) {
		if (animateInterpolation == null) throw new IllegalArgumentException("animateInterpolation cannot be null.");
		this.animateInterpolation = animateInterpolation;
	}

	public void setDisabled (boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isDisabled () {
		return disabled;
	}

	
	static public class ReloadWidgetStyle {
		public Drawable pixel;
		
		public ReloadWidgetStyle () {
		}

		public ReloadWidgetStyle (Drawable pixel) {
			this.pixel = pixel;
		}

		public ReloadWidgetStyle (ReloadWidgetStyle style) {
			this.pixel = style.pixel;
		}
	}
}
