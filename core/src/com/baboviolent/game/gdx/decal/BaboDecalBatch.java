package com.baboviolent.game.gdx.decal;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.GroupStrategy;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SortedIntList;

public class BaboDecalBatch {
	private static final int DEFAULT_SIZE = 1000;
	private float[] vertices;
	private Mesh mesh;
	protected final RenderContext context;
	protected final boolean ownContext;

	private final SortedIntList<Array<BaboDecal>> groupList = new SortedIntList<Array<BaboDecal>>();
	private BaboGroupStrategy groupStrategy;
	private final Pool<Array<BaboDecal>> groupPool = new Pool<Array<BaboDecal>>(16) {
		@Override
		protected Array<BaboDecal> newObject () {
			return new Array<BaboDecal>(false, 100);
		}
	};
	private final Array<Array<BaboDecal>> usedGroups = new Array<Array<BaboDecal>>(16);

	
	public BaboDecalBatch(BaboGroupStrategy groupStrategy) {
		this(groupStrategy, null);
	}
	
	public BaboDecalBatch(BaboGroupStrategy groupStrategy, RenderContext context) {
		this(DEFAULT_SIZE, groupStrategy, context);
	}

	public BaboDecalBatch(int size, BaboGroupStrategy groupStrategy, RenderContext context) {
		this.ownContext = (context == null);
		this.context = (context == null) ? new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1)) : context;
		initialize(size);
		setGroupStrategy(groupStrategy);
	}

	/** Sets the {@link GroupStrategy} used
	 * @param groupStrategy Group strategy to use */
	public void setGroupStrategy (BaboGroupStrategy groupStrategy) {
		this.groupStrategy = groupStrategy;
	}

	/** Initializes the batch with the given amount of decal objects the buffer is able to hold when full.
	 * 
	 * @param size Maximum size of decal objects to hold in memory */
	public void initialize (int size) {
		vertices = new float[size * Decal.SIZE];
		mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, size * 4, size * 6, new VertexAttribute(
			VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(
			VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(
			VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

		short[] indices = new short[size * 6];
		int v = 0;
		for (int i = 0; i < indices.length; i += 6, v += 4) {
			indices[i] = (short)(v);
			indices[i + 1] = (short)(v + 2);
			indices[i + 2] = (short)(v + 1);
			indices[i + 3] = (short)(v + 1);
			indices[i + 4] = (short)(v + 2);
			indices[i + 5] = (short)(v + 3);
		}
		mesh.setIndices(indices);
	}

	/** @return maximum amount of decal objects this buffer can hold in memory */
	public int getSize () {
		return vertices.length / Decal.SIZE;
	}

	/** Add a decal to the batch, marking it for later rendering
	 * 
	 * @param decal Decal to add for rendering */
	public void add (BaboDecal decal) {
		int groupIndex = groupStrategy.decideGroup(decal);
		Array<BaboDecal> targetGroup = groupList.get(groupIndex);
		if (targetGroup == null) {
			targetGroup = groupPool.obtain();
			targetGroup.clear();
			usedGroups.add(targetGroup);
			groupList.insert(groupIndex, targetGroup);
		}
		targetGroup.add(decal);
	}

	/** Flush this batch sending all contained decals to GL. After flushing the batch is empty once again. */
	public void flush () {
		if( ownContext ) context.begin();
		render();
		clear();
		if( ownContext ) context.end();
	}

	/** Renders all decals to the buffer and flushes the buffer to the GL when full/done */
	protected void render () {
		groupStrategy.beforeGroups();
		for (SortedIntList.Node<Array<BaboDecal>> group : groupList) {
			groupStrategy.beforeGroup(group.index, group.value);
			ShaderProgram shader = groupStrategy.getGroupShader(group.index);
			render(shader, group.value);
			groupStrategy.afterGroup(group.index);
		}
		groupStrategy.afterGroups();
	}

	/** Renders a group of vertices to the buffer, flushing them to GL when done/full
	 * 
	 * @param decals Decals to render */
	private void render (ShaderProgram shader, Array<BaboDecal> decals) {
		// batch vertices
		BaboDecalMaterial lastMaterial = null;
		int idx = 0;
		int lastTextureNumber = 0;
		for (BaboDecal decal : decals) {
			if (lastMaterial == null || !lastMaterial.equals(decal.getMaterial())) {
				if (idx > 0) {
					flush(shader, idx, lastTextureNumber);
					idx = 0;
				}
				lastTextureNumber = decal.material.set(context);
				lastMaterial = decal.material;
			}
			decal.update();
			System.arraycopy(decal.vertices, 0, vertices, idx, decal.vertices.length);
			idx += decal.vertices.length;
			// if our batch is full we have to flush it
			if (idx == vertices.length) {
				flush(shader, idx, lastTextureNumber);
				idx = 0;
			}
		}
		// at the end if there is stuff left in the batch we render that
		if (idx > 0) {
			flush(shader, idx, lastTextureNumber);
		}
	}

	/** Flushes vertices[0,verticesPosition[ to GL verticesPosition % Decal.SIZE must equal 0
	 * 
	 * @param verticesPosition Amount of elements from the vertices array to flush */
	protected void flush (ShaderProgram shader, int verticesPosition, int textureNumber) {
		groupStrategy.beforeFlush(textureNumber);
		mesh.setVertices(vertices, 0, verticesPosition);
		mesh.render(shader, GL20.GL_TRIANGLES, 0, verticesPosition / 4);
	}

	/** Remove all decals from batch */
	protected void clear () {
		groupList.clear();
		groupPool.freeAll(usedGroups);
		usedGroups.clear();
	}

	/** Frees up memory by dropping the buffer and underlying resources. If the batch is needed again after disposing it can be
	 * {@link #initialize(int) initialized} again. */
	public void dispose () {
		clear();
		vertices = null;
		mesh.dispose();
	}
}
