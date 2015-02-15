package com.baboviolent.game.menu.main;

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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;


public class MenuShader implements Disposable {
	private ShaderProgram program;
	private int u_time;
	private int u_resolution;
	private int u_alpha;
	private float width;
	private float height;
	private Mesh mesh;
	private float slowAnimation = 3;
	
	public void init(float width, float height) {
		String p = BaboViolentGame.PATH_SHADERS;
        String vert = Gdx.files.internal(p+"/menu2.vertex.glsl").readString();
        String frag = Gdx.files.internal(p+"/menu2.fragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        u_time = program.getUniformLocation("u_time");
        u_resolution = program.getUniformLocation("u_resolution");
        u_alpha = program.getUniformLocation("u_alpha");
        this.width = width;
        this.height = height;
        
        mesh = new Mesh(true, 6, 6, new VertexAttribute(Usage.Position, 2, "a_position"));
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

	public void begin(float time) {
		begin(time, 1f);
	}
	
	public void begin(float time, float alpha) {
		if( alpha < 0 ) {
			alpha = 0;
		}
		
		time = time/slowAnimation;
		program.begin();
		program.setUniformf(u_time, time/1000f);
		program.setUniformf(u_alpha, alpha);
		program.setUniformf(u_resolution, width, height);
	}

	public void render() {
		mesh.render(program, GL20.GL_TRIANGLES);
	}

	public void end() {
		program.end();
	}
	
	@Override
	public void dispose() {
		mesh.dispose();
		program.dispose();
	}
}
