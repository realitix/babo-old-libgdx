#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D texture0;
uniform sampler2D texture1;

varying vec2 v_diffuseUV;
varying float v_range;

void main( void )
{
	float r = v_diffuseUV.x;
	float g = v_diffuseUV.y;
	
	if( v_range == 1 ) {
		r = 1.0 - v_diffuseUV.x;  
		g = 1.0 - v_diffuseUV.y;  
	}
	
	gl_FragColor = vec4(v_range, 0.0, 0.0, 1.0);
}