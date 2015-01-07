package com.baboviolent.game.particle;

import com.baboviolent.game.particle.effects.BaboParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class PoolParticle extends Pool<BaboParticleEffect> {
    private BaboParticleEffect sourceEffect;
    private Array<BaboParticleEffect> activeEffects = new Array<BaboParticleEffect>();
    private BaboParticleSystem particleSystem;
    
    public PoolParticle (BaboParticleEffect sourceEffect, BaboParticleSystem ps) {
        this.sourceEffect = sourceEffect;
        this.particleSystem = ps;
    }
    
    public Array<BaboParticleEffect> getActiveEffects() {
        return activeEffects;
    }

    @Override
    public void free(BaboParticleEffect effect) {
        activeEffects.removeValue(effect, true);
        effect.reset();
        super.free(effect);
    }

    @Override
    protected BaboParticleEffect newObject() {
        return sourceEffect.copy();
    }
    
    @Override
    public BaboParticleEffect obtain() {
    	BaboParticleEffect e = super.obtain();
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
            Array<ParticleController> c = activeEffects.get(i).getControllers();
            boolean isActive = false;
            // Parcours les controllers
            for( int j = 0; j < c.size; j++ ) {
                if( c.get(j).emitter instanceof RegularEmitter ) {
                    RegularEmitter emitter = (RegularEmitter) c.get(j).emitter;
                    if( !emitter.isComplete() ) {
                        isActive = true;
                    }
                }
            }
            
            // Si effet non actif, on le supprime et remet dans le pool
            if( !isActive ) {
            	BaboParticleEffect p = activeEffects.get(i);
                particleSystem.remove(p);
                this.free(p);
                activeEffects.removeValue(p, true);
            }
        }
    }
}