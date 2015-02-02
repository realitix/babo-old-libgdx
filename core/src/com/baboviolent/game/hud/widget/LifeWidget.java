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

public class LifeWidget extends Widget implements Disableable {
	private LifeWidgetStyle style;
	private float min, max, stepSize;
	private float value, animateFromValue;
	float position;
	final boolean vertical;
	private float animateDuration, animateTime;
	private Interpolation animateInterpolation = Interpolation.linear;
	private float[] snapValues;
	private float threshold;
	boolean disabled;
	boolean shiftIgnoresSnap;

	public LifeWidget (float min, float max, float stepSize, boolean vertical, Skin skin) {
		this(min, max, stepSize, vertical, skin.get("default-" + (vertical ? "vertical" : "horizontal"), LifeWidgetStyle.class));
	}

	public LifeWidget (float min, float max, float stepSize, boolean vertical, Skin skin, String styleName) {
		this(min, max, stepSize, vertical, skin.get(styleName, LifeWidgetStyle.class));
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
	public LifeWidget (float min, float max, float stepSize, boolean vertical, LifeWidgetStyle style) {
		if (min > max) throw new IllegalArgumentException("max must be > min. min,max: " + min + ", " + max);
		if (stepSize <= 0) throw new IllegalArgumentException("stepSize must be > 0: " + stepSize);
		setStyle(style);
		this.min = min;
		this.max = max;
		this.stepSize = stepSize;
		this.vertical = vertical;
		this.value = min;
		setSize(getPrefWidth(), getPrefHeight());
	}

	public void setStyle (LifeWidgetStyle style) {
		if (style == null) throw new IllegalArgumentException("style cannot be null.");
		this.style = style;
		invalidateHierarchy();
	}

	/** Returns the progress bar's style. Modifying the returned style may not have an effect until
	 * {@link #setStyle(ProgressBarStyle)} is called. */
	public LifeWidgetStyle getStyle () {
		return style;
	}

	@Override
	public void act (float delta) {
		super.act(delta);
		if (animateTime > 0) {
			animateTime -= delta;
			Stage stage = getStage();
			if (stage != null && stage.getActionsRequestRendering()) Gdx.graphics.requestRendering();
		}
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		LifeWidgetStyle style = this.style;
		boolean disabled = this.disabled;
		final Drawable bg = style.background;
		final Drawable top = style.top;
		
		Color color = getColor();
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
		float value = getVisualValue();

		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		bg.draw(batch, x, y + (int)((height - bg.getMinHeight()) * 0.5f), width, height);
		top.draw(batch, x, y + (int)((height - bg.getMinHeight()) * 0.5f), width, height);		
	}

	public float getValue () {
		return value;
	}

	/** If {@link #setAnimateDuration(float) animating} the progress bar value, this returns the value current displayed. */
	public float getVisualValue () {
		if (animateTime > 0) return animateInterpolation.apply(animateFromValue, value, 1 - animateTime / animateDuration);
		return value;
	}

	/** Sets the progress bar position, rounded to the nearest step size and clamped to the minimum and maximum values.
	 * {@link #clamp(float)} can be overridden to allow values outside of the progress bar's min/max range.
	 * @return false if the value was not changed because the progress bar already had the value or it was canceled by a listener. */
	public boolean setValue (float value) {
		value = clamp(Math.round(value / stepSize) * stepSize);
		if (!shiftIgnoresSnap || (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)))
			value = snap(value);
		float oldValue = this.value;
		if (value == oldValue) return false;
		float oldVisualValue = getVisualValue();
		this.value = value;
		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		boolean cancelled = fire(changeEvent);
		if (cancelled)
			this.value = oldValue;
		else if (animateDuration > 0) {
			animateFromValue = oldVisualValue;
			animateTime = animateDuration;
		}
		Pools.free(changeEvent);
		return !cancelled;
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
		final Drawable bg = style.background;
		return bg.getMinWidth();
		
	}

	public float getPrefHeight () {
		final Drawable bg = style.background;
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

	/** Will make this progress bar snap to the specified values, if the knob is within the threshold. */
	public void setSnapToValues (float[] values, float threshold) {
		this.snapValues = values;
		this.threshold = threshold;
	}

	/** Returns a snapped value. */
	private float snap (float value) {
		if (snapValues == null) return value;
		for (int i = 0; i < snapValues.length; i++) {
			if (Math.abs(value - snapValues[i]) <= threshold) return snapValues[i];
		}
		return value;
	}

	public void setDisabled (boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isDisabled () {
		return disabled;
	}

	/** The style for a progress bar, see {@link ProgressBar}.
	 * @author mzechner
	 * @author Nathan Sweet */
	static public class LifeWidgetStyle {
		/** The progress bar background, stretched only in one direction. */
		public Drawable background;
		/** Optional. **/
		public Drawable top;
		
		public LifeWidgetStyle () {
		}

		public LifeWidgetStyle (Drawable background, Drawable top) {
			this.background = background;
			this.top = top;
		}

		public LifeWidgetStyle (LifeWidgetStyle style) {
			this.background = style.background;
			this.top = style.top;
		}
	}
}
