package com.baboviolent.game.particle;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class PoolParticle extends Pool<ParticleEffect> {
    private ParticleEffect sourceEffect;
    private Array<ParticleEffect> activeEffects = new Array<ParticleEffect>();

    public PoolParticle (ParticleEffect sourceEffect) {
        this.sourceEffect = sourceEffect;
    }
    
    public Array<ParticleEffect> getActiveEffects() {
        return activeEffects;
    }

    @Override
    public void free(ParticleEffect effect) {
        activeEffects.removeValue(effect, true);
        effect.reset();
        super.free(effect);
    }

    @Override
    protected ParticleEffect newObject() {
    	System.out.println("test2");
        return sourceEffect.copy();
    }
    
    @Override
    public ParticleEffect obtain() {
    	System.out.println("test1");
        ParticleEffect e = super.obtain();
        activeEffects.add(e);
        return e;
    }
    
    /**
     * Met a jour les particules
     * Si un effet est terminé, on le supprime du particlesystem et on remet dans le pool
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
            	System.out.println("Suppression de la particule");
                ParticleEffect p = activeEffects.get(i);
                ParticleSystem.get().remove(p);
                this.free(p);
                activeEffects.removeValue(p, true);
            }
        }
    }
}