uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

attribute vec3 a_position; 
attribute vec2 a_texCoord;
attribute float a_texRate;

varying vec2 v_diffuseUV;
varying float v_diffuseRate;

void main() {
	v_diffuseUV = a_texCoord;
	v_diffuseRate = a_texRate;
	gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}