/*******************************************************************************
 * Copyright 2012 bmanuel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.baboviolent.game.map.editor;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.LongMap;

public final class ResourceFactory {
	public static boolean DebugUI = true;
	public static Skin UISkin;
	private static ItemsManager<Texture> textures = new ItemsManager<Texture>();
	private static LongMap<Texture> textureCache = new LongMap<Texture>();
	public static int width; 
	public static int height;
	
	public static void initSkin() {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		
		String p = BaboViolentGame.path(BaboViolentGame.PATH_EDITOR);
		String size = new String();
		size = "40";
		
		if( width < 1920 )
			size ="30";
		if( width < 1024 )
			size = "20";
		if( width < 800 )
			size = "13";
		
		UISkin = new Skin( Gdx.files.internal( p + "skin/uiskin_"+size+".json" ) );
	}

	public static Texture newTexture( String name, boolean mipMap ) {
		long hash = Hash.APHash( name );
		Texture t = textureCache.get( hash );

		if( t != null ) {
			return t;
		}

		t = new Texture( Gdx.files.internal( BaboViolentGame.path(BaboViolentGame.PATH_EDITOR) + name ), Format.RGBA8888, mipMap );

		if( mipMap ) {
			t.setFilter( TextureFilter.MipMapLinearNearest, TextureFilter.Nearest );
		} else {
			t.setFilter( TextureFilter.Nearest, TextureFilter.Nearest );
		}

		textures.add( t );
		textureCache.put( hash, t );

		return t;
	}

	public static Sprite newSprite( String textureName ) {
		Sprite s = new Sprite( newTexture( textureName, false ) );
		s.flip( false, true );
		return s;
	}

	// ui

	public static Slider newSlider( float min, float max, float step, float value ) {
		return newSlider( min, max, step, value, null );
	}

	public static Slider newSlider( float min, float max, float step, float value, ChangeListener listener ) {
		Slider s = new Slider( min, max, step, false, UISkin );
		s.setValue( value );
		if( listener != null ) {
			s.addListener( listener );
		}
		return s;
	}

	public static CheckBox newCheckBox( String text, boolean checked ) {
		return newCheckBox( text, checked, null );
	}

	public static CheckBox newCheckBox( String text, boolean checked, ClickListener listener ) {
		CheckBox cb = new CheckBox( text, UISkin );
		cb.setChecked( checked );
		if( listener != null ) {
			cb.addListener( listener );
		}
		return cb;
	}

	public static SelectBox<String> newSelectBox( String[] items ) {
		return newSelectBox( items, null );
	}

	public static SelectBox<String> newSelectBox( String[] items, ChangeListener listener ) {
		SelectBox<String> sb = new SelectBox<String>( UISkin );
		if( listener != null ) {
			sb.addListener( listener );
		}

		sb.setItems( items );
		return sb;
	}

	public static Label newLabel( String text ) {
		Label l = new Label( text, UISkin );
		return l;
	}

	public static Table newTable() {
		Table t = new Table();
		if( DebugUI ) {
			t.debug();
		}
		return t;
	}

	public static TextButton newButton( String text ) {
		return newButton( text, null );
	}

	public static TextButton newButton( String text, ClickListener listener ) {
		TextButton b = new TextButton( text, UISkin );
		if( listener != null ) {
			b.addListener( listener );
		}
		return b;
	}
	
	public static Dialog newDialog( String text ) {
		Dialog d = new Dialog(text, UISkin);
		
		d.setPosition(width/2 - d.getWidth() / 2, height/2 - d.getHeight()/2);
		return d;
	}
	
	public static TextField newTextField( String text ) {
		TextField t = new TextField(text, UISkin);
		
		return t;
	}

	public static void dispose() {
		textures.dispose();
		textureCache.clear();
	}

	private ResourceFactory() {
	}
}
