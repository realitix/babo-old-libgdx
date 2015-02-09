package com.baboviolent.game.shader;

import com.baboviolent.game.BaboViolentGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;


public class MenuShader {
	private ShaderProgram program;
	private int u_time;
	private int u_resolution;
	private float width;
	private float height;
	private Mesh mesh;
	private float slowAnimation = 3;
	
	public void init(float width, float height) {
		String p = BaboViolentGame.PATH_SHADERS;
        String vert = Gdx.files.internal(p+"/menu.vertex.glsl").readString();
        String frag = Gdx.files.internal(p+"/menu.fragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        u_time = program.getUniformLocation("u_time");
        u_resolution = program.getUniformLocation("u_resolution");
        this.width = width;
        this.height = height;
        
        mesh = new Mesh(false, 6, 6, new VertexAttribute(Usage.Position, 2, "a_position"));
        // On fait deux triangles pour former un carre
        // L'origine est le milieu de l'ecran
        mesh.setVertices(new float[] { -width/2, -height/2,
                width/2, -height/2,
                -width/2, height/2,
                -width/2, height/2,
                width/2, height/2,
                width/2, -height/2});
        mesh.setIndices(new short[] { 0, 1, 2, 3, 4, 5 });
	}

	public void dispose() {
		program.dispose();
	}

	public void begin(float time) {
		time = time/slowAnimation;
		program.begin();
		program.setUniformf(u_time, time/1000f);
		program.setUniformf(u_resolution, width, height);
	}

	public void render() {
		mesh.render(program, GL20.GL_TRIANGLES, 0, 6);
	}

	public void end() {
		program.end();
	}
}
