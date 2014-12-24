package com.baboviolent.game.map.editor;

import com.baboviolent.game.BaboViolentGame;
import com.baboviolent.game.loader.TextureLoader;
import com.baboviolent.game.map.Map;
import com.baboviolent.game.screen.MapEditorScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Implements the UI creation and event handling.
 *
 * Notes on the panel animator: some very simple expedients help in determining whenever the
 * user is voluntarily leaving the panel area to make it to hide itself or it's just due to
 * the fact combobox widgets are higher than the panel, thus the user *needs* to move out of
 * the panel to make a selection: the latter case is being tracked by
 */
public final class UI {
	private UiStage stage;
	private final MapEditorScreen screen;

	// panel animator
	private boolean comboBoxFlag, panelShown, usePanelAnimator;
	private TopPanelAnimator panelAnimator;
	private String[] styles;
	private String selectedStyle = new String();
	
	private String[] types = {"Ground 1", "Ground 2", "Wall"};
	private String selectedType = new String(types[0]);
	
	private static final boolean DebugUI = false;
	private Array<SelectBox<String>> selectBoxes = new Array<SelectBox<String>>();
	private final TextField textfield;
	private SelectBox<String> selectBox;
	private String loadMapName;
	
	public UI( final MapEditorScreen screen, boolean panelAutoShow ) {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		this.screen = screen;

		ResourceFactory.initSkin();
		ResourceFactory.DebugUI = DebugUI;
		
		textfield = ResourceFactory.newTextField("");
		selectBox = null;

		comboBoxFlag = false;
		usePanelAnimator = panelAutoShow;
		initStyles();
		stage = new UiStage(screen);
		if( DebugUI ) {
			stage.setDebugAll( true );
		}

		// panel background
		NinePatch np = new NinePatch( ResourceFactory.newTexture( "brushed.png", false ), 0, 0, 0, 0 );
		np.setColor( new Color( 0.3f, 0.3f, 0.3f, 1f ) );
		NinePatchDrawable npBack = new NinePatchDrawable( np );

		// build the top panel and add all of its widgets
		Table topPanel = buildTopPanel( npBack, width, height );
		topPanel.add( buildStylesWidget() );
		topPanel.add( buildTypesWidget() );
		topPanel.add( buildSaveWidget() );
		topPanel.add( buildLoadWidget() );
		

		// compute the panel's opened/closed position
		final float yWhenShown = height - topPanel.getHeight() + 13;
		final float yWhenHidden = height - 60 + 13;
				
		if( usePanelAnimator ) {
			panelShown = false;
			panelAnimator = new TopPanelAnimator( topPanel, new Rectangle( 10, 5, width - 20, 60 ), yWhenShown, yWhenHidden );
			topPanel.setY( yWhenHidden );
			topPanel.setColor( 1f, 1f, 1f, 0.5f );
		} else {
			panelShown = true;
			topPanel.setY( yWhenShown );
		}
		
		Table bottomPanel = buildBottomPanel( npBack, width, height );
		bottomPanel.add( buildInputWidget() );
		

		// UI is quite ready at this point, just add the containers to the stage
		stage.addActor( topPanel );
		stage.addActor( bottomPanel );

		// perform some processing on the SelectBox widgets
		for( int i = 0; i < selectBoxes.size; i++ ) {
			// fire a change event on selected SelectBoxes to
			// update the default selection and initialize accordingly
			selectBoxes.get( i ).fire( new ChangeListener.ChangeEvent() );

			if( usePanelAnimator ) {
				// track clicks on the comboboxes, this imply the widget is
				// opening giving the user some choices
				selectBoxes.get( i ).addListener( new ClickListener() {
					@Override
					public void clicked( InputEvent event, float x, float y ) {
						comboBoxFlag = true;
						panelAnimator.suspend();
					}
				} );

				// track changes, user performed a selection
				selectBoxes.get( i ).addListener( new ChangeListener() {
					@Override
					public void changed( ChangeEvent event, Actor actor ) {
						comboBoxFlag = false;
					}
				} );
			}
		}

		// finally, track clicks on the stage, whenever the user cancel an
		// opened combobox selection by clicking away it will cause the widgets
		// to close (no ChangeListener will be notified)
		if( usePanelAnimator ) {
			stage.addListener( new ClickListener() {
				@Override
				public void clicked( InputEvent event, float x, float y ) {
					if( !comboBoxFlag ) {
						panelAnimator.resume();
					}
	
					comboBoxFlag = false;
				}
			} );
		}
	}

	private Table buildTopPanel( NinePatchDrawable back, float width, float height ) {

		Table p = ResourceFactory.newTable();
		p.setSize( width, height/5 );
		p.defaults().pad( 5, 25, 5, 0 ).align( Align.top );
		p.left();
		p.setBackground( back );

		return p;
	}
	
	private void initStyles() {
		Array<String> s = TextureLoader.listTextureFolder(TextureLoader.TYPE_WALL);
		styles = new String[s.size + 1];
		styles[0] = "Clear";
		for( int i = 0; i < s.size; i++ ) {
			styles[i+1] = s.get(i);
		}
		
		selectedStyle = styles[0];
	}

	private Table buildStylesWidget() {

		final SelectBox<String> sbStyles = ResourceFactory.newSelectBox( styles, new ChangeListener() {
			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				@SuppressWarnings( "unchecked" )
				SelectBox<String> source = (SelectBox<String>)actor;
				
				selectedStyle = source.getSelected();
				if( selectedStyle.equals("Clear") ) {
					screen.selectEraser();
					return;
				}
				
				if( selectedType.equals("Wall") )
					screen.selectWall(selectedStyle);
				if( selectedType.equals("Ground 1") )
					screen.selectGround(selectedStyle+"_1");
				if( selectedType.equals("Ground 2") )
					screen.selectGround(selectedStyle+"_2");
			}
		} );
		
		sbStyles.setSelectedIndex( 0 );
		selectBoxes.add( sbStyles );
		Table t = ResourceFactory.newTable();
		t.add( ResourceFactory.newLabel( "Style " ) );
		t.add( sbStyles );
		
		return t;
	}
	
	private Table buildTypesWidget() {
		
		final SelectBox<String> sbTypes = ResourceFactory.newSelectBox( types, new ChangeListener() {
			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				@SuppressWarnings( "unchecked" )
				SelectBox<String> source = (SelectBox<String>)actor;
				selectedType = source.getSelected();;
				if( selectedStyle.equals("Clear") ) {
					screen.selectEraser();
					return;
				}
				
				if( selectedType.equals("Wall") )
					screen.selectWall(selectedStyle);
				if( selectedType.equals("Ground 1") )
					screen.selectGround(selectedStyle+"_1");
				if( selectedType.equals("Ground 2") )
					screen.selectGround(selectedStyle+"_2");
			}
		} );

		sbTypes.setSelectedIndex( 0 );
		selectBoxes.add( sbTypes );

		Table t = ResourceFactory.newTable();
		t.add( ResourceFactory.newLabel( "Type " ) );
		t.add( sbTypes );
		return t;
	}
	
	private TextButton buildSaveWidget() {
		
		final TextButton tb = ResourceFactory.newButton( "Save", new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				textfield.setText("");
				Dialog d = new Dialog("Save", ResourceFactory.UISkin) {
					protected void result(java.lang.Object object) {
						if( object.equals(true) ) {
							screen.saveMap(textfield.getText());
						}
					}
				};
				d.setWidth((float) (ResourceFactory.width/1.5));
				d.text(ResourceFactory.newLabel( Gdx.files.getExternalStoragePath() ));
				
				d.button("Ok", true);
				d.button("Cancel", false);
				d.getContentTable().row();
				d.getContentTable().add(textfield);
				d.setPosition(ResourceFactory.width/2 - d.getWidth() / 2, ResourceFactory.height/2 - d.getHeight()/2);
				stage.addActor(d);
			}
		} );

		return tb;
	}
	
	private TextButton buildLoadWidget() {
		selectBox = ResourceFactory.newSelectBox( loadMapName(), new ChangeListener() {
			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				@SuppressWarnings( "unchecked" )
				SelectBox<String> source = (SelectBox<String>)actor;
				loadMapName = source.getSelected();
			}
		} );
		
		final TextButton tb = ResourceFactory.newButton( "Load", new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Dialog d = new Dialog("Load", ResourceFactory.UISkin) {
					protected void result(java.lang.Object object) {
						if( object.equals(true) ) {
							screen.loadMap(loadMapName);
						}
					}
				};
				d.setWidth((float) (ResourceFactory.width/1.5));
				
				d.button("Ok", true);
				d.button("Cancel", false);
				d.getContentTable().add(selectBox);
				d.setPosition(ResourceFactory.width/2 - d.getWidth() / 2, ResourceFactory.height/2 - d.getHeight()/2);
				stage.addActor(d);
			}
		} );

		return tb;
	}
	
	private Table buildBottomPanel( NinePatchDrawable back, float width, float height ) {
		Table t = ResourceFactory.newTable();
		t.setSize( width, height/4 );
		t.defaults().pad( 10, 15, 0, 15 ).align( Align.top ).expandY();
		t.setY( -98 );
		t.left();
		t.setBackground( back );

		return t;
	}

	
	private TextButton buildInputWidget() {
		final TextButton tb = ResourceFactory.newButton( "Input", new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				screen.switchInput();
			}
		} );
		return tb;
	}
	
	private String[] loadMapName() {
		Array<String> maps = new Array<String>();	    
	    FileHandle[] files = Gdx.files.external("").list();
        for(FileHandle file: files) {
        	if( file.extension().equals(BaboViolentGame.EXTENSION_MAP) ) {
        		maps.add(file.nameWithoutExtension());
        	}
        }
       String[] result = new String[maps.size];
       for(int i = 0; i < maps.size; i++) {
    	   result[i] = maps.get(i);
       }
       
       return result;
	}

	public void update( float deltaTimeSecs ) {
		stage.act( deltaTimeSecs );
		if( usePanelAnimator ) {
			panelAnimator.update();
		}
	}

	public void draw() {
		stage.draw();
	}

	public void mouseMoved( int x, int y ) {
		if( usePanelAnimator ) {
			panelAnimator.mouseMoved( x, y );
		}
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public class UiStage extends Stage {
    	final MapEditorScreen s;
    	private Vector2 lastTouchDown = new Vector2();
    	public float maxTouchdistance = 20;
    	
		public UiStage(final MapEditorScreen s) {
			super();
			this.s = s;
    	}
		
		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			super.mouseMoved(screenX, screenY);
	        s.mouseMove(screenX, screenY);
	        return false;
	    }
		
		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			super.touchDown(screenX, screenY, pointer, button);
			
			if(button == Buttons.LEFT) {
	    		lastTouchDown.set(screenX, screenY);
	    	}
			
			return false;
	    }
		
		@Override
		public boolean touchDragged (int screenX, int screenY, int pointer) {
			return false;
		}
		
		@Override
		public boolean scrolled(int amount) {
			return false;
		}
		
		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			super.touchUp(screenX, screenY, pointer, button);
	    	Vector2 v = screenToStageCoordinates(new Vector2(screenX, screenY));
	    	if(hit(v.x, v.y, false) != null) {
	    		return false;
	    	}
	    	
	    	// On ne compte que le bouton gauche
			if(button != Buttons.LEFT) {
	    		return false;
	    	}
			
			// On ne clique pas si le relachement est loin de l'appuie
			if(lastTouchDown.dst(screenX, screenY) > maxTouchdistance ) {
				return false;
			}
	    	
	    	s.mouseClick(screenX, screenY);
	    	return false;
	    }
    }
}
