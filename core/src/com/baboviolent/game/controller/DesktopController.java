package com.baboviolent.game.controller;

public class DesktopController extends BaseController {
    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Keys.LEFT:
                player.getInstance().transform.rotate(0, 1, 0, 5f);
			    ghostObject.setWorldTransform(player.getInstance().transform);
			    break;
			case Keys.RIGHT:
                player.getInstance().transform.rotate(0, 1, 0, -5f);
			    ghostObject.setWorldTransform(player.getInstance().transform);
			    break;
        }
        
        Vector3 playerDirection = new Vector3()
            .set(-1,0,0)
            .rot(player.getInstance().transform)
            .nor();
        Vector3 walkDirection = new Vector3().set(0,0,0);
        
        switch(keycode) {
            case Keys.UP:
                walkDirection.add(playerDirection);
                break;
            case Keys.DOWN:
                walkDirection.add(-playerDirection.x, -playerDirection.y, -playerDirection.z);
                break;
        }
        
		walkDirection.scl(4f * Gdx.graphics.getDeltaTime());
		// And update the character controller
		bulletController.setWalkDirection(walkDirection);
    }
}