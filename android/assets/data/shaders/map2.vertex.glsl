uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_shadowMapProjViewTrans;

attribute vec3 a_position; 
attribute vec2 a_texCoord;

varying vec2 v_diffuseUV;
varying vec3 v_shadowMapUv;

void main() {
	vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	vec4 spos = u_shadowMapProjViewTrans * pos;

	v_shadowMapUv.xy = (spos.xy / spos.w) * 0.5 + 0.5;
	v_shadowMapUv.z = min(spos.z * 0.5 + 0.5, 0.998);
	v_diffuseUV = a_texCoord;

	gl_Position = u_projViewTrans * pos;
}