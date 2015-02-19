package com.baboviolent.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

public class Configuration {
	
	public static final int MIN = 1;
	public static final int MED = 2;
	public static final int MAX = 3;
	
	public static class ConfigurationAdapter {
		long startTime;

		public ConfigurationAdapter() {
			startTime = TimeUtils.millis();
		}

		public void update() {
			if (TimeUtils.timeSinceMillis(startTime) > 1000) {
				System.out.println(Gdx.graphics.getFramesPerSecond());
				if( Gdx.graphics.getFramesPerSecond() <= 59 ) {
					Video.enableShadow = false;
				}
				else {
					Video.enableShadow = true;
				}
				startTime = TimeUtils.millis();
			}
		}
	}
	
	public static class Video {
		public static int baboLevelOfDetail = Configuration.MED;
		public static boolean enableShadow = true;
	}
}
