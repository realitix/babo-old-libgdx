uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

attribute vec3 a_position; 
attribute vec2 a_texCoord;
attribute float a_range;

varying vec2 v_diffuseUV;
varying float v_range;

void main() {
	v_diffuseUV = a_texCoord;
	v_range = a_range;
	gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}