uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_shadowMapProjViewTrans;
uniform vec2 u_center;
uniform vec2 u_screenSize;
uniform vec2 u_mapSize;

attribute vec3 a_position; 
attribute vec2 a_texCoord;
attribute float u_side;

varying vec2 v_diffuseUV;
varying vec2 v_alphaMapUV;
varying vec3 v_shadowMapUv;

void main() {
	vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	vec4 spos = u_shadowMapProjViewTrans * pos;
	v_shadowMapUv.xy = (spos.xy / spos.w) * 0.5 + 0.5;
	v_shadowMapUv.z = min(spos.z * 0.5 + 0.5, 0.998);
	v_diffuseUV = a_texCoord;
	v_alphaMapUV.x = 1 - pos.x / u_mapSize.x;
	v_alphaMapUV.y = pos.z / u_mapSize.y;

	gl_Position = u_projViewTrans * pos;
}