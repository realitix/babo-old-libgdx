package com.baboviolent.game.mode;

import com.baboviolent.game.bullet.BulletWorld;
import com.baboviolent.game.gameobject.Babo;
import com.badlogic.gdx.math.Vector2;

public class BaseMode {
    protected final String mapName;
    
    public BaseMode(final String mapName) {
        this.mapName = mapName;
    }
    
    public BulletInstance getMapInstance() {
        return Map.loadInstance(mapName);
    }
}