#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;

varying vec2 v_diffuseUV;
varying float v_range;

void main( void )
{	
	gl_FragColor = texture2D(u_diffuseTexture, v_diffuseUV);
	//gl_FragColor = vec4(v_diffuseUV.xy, 0.0, 1.0);
}