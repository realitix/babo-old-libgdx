package com.baboviolent.game.effect.light;

import com.baboviolent.game.effect.light.effects.BaboLightEffect;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class PoolLight extends Pool<BaboLightEffect> {
    private BaboLightEffect sourceEffect;
    private Array<BaboLightEffect> activeEffects = new Array<BaboLightEffect>();
    private BaboLightSystem lightSystem;
    
    public PoolLight (BaboLightEffect sourceEffect, BaboLightSystem ls) {
        this.sourceEffect = sourceEffect;
        this.lightSystem = ls;
    }
    
    public Array<BaboLightEffect> getActiveEffects() {
        return activeEffects;
    }

    @Override
    public void free(BaboLightEffect effect) {
        activeEffects.removeValue(effect, true);
        effect.reset();
        super.free(effect);
    }

    @Override
    protected BaboLightEffect newObject() {
        return sourceEffect.copy();
    }
    
    @Override
    public BaboLightEffect obtain() {
    	BaboLightEffect e = super.obtain();
        activeEffects.add(e);
        return e;
    }
    
    /**
     * Met a jour les particules
     * Si un effet est termine, on le supprime du particlesystem et on remet dans le pool
     */
    public void update() {
        if( activeEffects.size == 0 )
            return;
        
        // Parcours les effets
        for( int i = 0; i < activeEffects.size; i++) {
            // Si effet non actif, on le supprime et remet dans le pool
            if( !activeEffects.get(i).isActive() ) {
            	BaboLightEffect e = activeEffects.get(i);
                lightSystem.remove(e);
                this.free(e);
                activeEffects.removeValue(e, true);
            }
        }
    }
}