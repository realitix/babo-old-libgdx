package com.baboviolent.game.controller.scene2d;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Touchpad modifie permettant d'obtenir le meme comportement que dans le jeu
 * minigore2
 * A savoir, le fond ne s'affiche que lors de la touche
 *
 */
public class BaboTouchpad extends Touchpad {

	public BaboTouchpad(float deadzoneRadius, Skin skin) {
		super(deadzoneRadius, skin);
	}

	public BaboTouchpad(float deadzoneRadius, Skin skin, String styleName) {
		super(deadzoneRadius, skin, styleName);
	}

	public BaboTouchpad(float deadzoneRadius, TouchpadStyle style) {
		super(deadzoneRadius, style);
	}

	/**
	 * On modifie l'affichage pour n'afficher le fond seuelement lors de la touche
	 */
	@Override
	public void draw (Batch batch, float parentAlpha) {
		validate();
		TouchpadStyle style = super.getStyle();

		Color c = getColor();
		batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);

		float x = getX();
		float y = getY();
		float w = getWidth();
		float h = getHeight();

		if( isTouched() ) {
			final Drawable bg = style.background;
			if (bg != null) bg.draw(batch, x, y, w, h);
		}

		final Drawable knob = style.knob;
		if (knob != null) {
			x += getKnobX() - knob.getMinWidth() / 2f;
			y += getKnobY() - knob.getMinHeight() / 2f;
			knob.draw(batch, x, y, knob.getMinWidth(), knob.getMinHeight());
		}
	}
}
