package com.baboviolent.game.gdx.decal;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

public interface BaboGroupStrategy {
	/** Returns the shader to be used for the group. Can be null in which case the GroupStrategy doesn't support GLES 2.0
	 * @param group the group
	 * @return the {@link ShaderProgram} */
	public ShaderProgram getGroupShader (int group);

	/** Assigns a group to a decal
	 * 
	 * @param decal Decal to assign group to
	 * @return group assigned */
	public int decideGroup (BaboDecal decal);

	/** Invoked directly before rendering the contents of a group
	 * 
	 * @param group Group that will be rendered
	 * @param contents Array of entries of arrays containing all the decals in the group */
	public void beforeGroup (int group, Array<BaboDecal> contents);

	/** Invoked directly after rendering of a group has completed
	 * 
	 * @param group Group which completed rendering */
	public void afterGroup (int group);

	/** Invoked before rendering any group */
	public void beforeGroups ();

	/** Invoked after having rendered all groups */
	public void afterGroups ();
	
	public void beforeFlush (int textureNumber);
}