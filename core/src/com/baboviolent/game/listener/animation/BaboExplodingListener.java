package com.baboviolent.game.listener.animation;

public class BaboExplodingListener implements AnimationController.AnimationListener {
    private final Babo babo;
    
    public BaboExplodingListener(final Babo babo) {
        this.babo = babo;
    }
    
    public void onEnd(AnimationController.AnimationDesc animation) {
        babo.endExplode();
    }
    
    public void onLoop(AnimationController.AnimationDesc animation) {
    }
}
	