uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

attribute vec3 a_position; 
attribute vec2 a_texCoord;

varying vec2 v_diffuseUV;

void main() {
	vec4 pos = u_worldTrans * vec4(a_position, 1.0);

	v_diffuseUV = a_texCoord;

	gl_Position = u_projViewTrans * pos;
}