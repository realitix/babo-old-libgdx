uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec2 u_mapSize;
uniform vec3 u_cameraPosition;
#if (QUALITY == MAX)
	uniform mat4 u_shadowMapProjViewTrans;
#endif

attribute vec3 a_position; 
attribute vec2 a_texCoord;

varying vec2 v_diffuseUV;
varying vec2 v_alphaMapUV;
varying vec3 v_pos;
#if (QUALITY != MIN)
	varying vec3 v_viewVec;
	varying vec3 v_normal;
	varying vec3 v_binormal;
	varying vec3 v_tangent;
#endif
//varying vec3 v_lightSpecular;
#if (QUALITY == MAX)
	varying vec3 v_shadowMapUv;
#endif

/**
 Calculer binormal et tangente:
http://www.ozone3d.net/tutorials/mesh_deformer_p2.php#tangent_space
*/

void main() {
	vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	
	#if (QUALITY == MAX)
		vec4 spos = u_shadowMapProjViewTrans * pos;
		v_shadowMapUv.xy = (spos.xy / spos.w) * 0.5 + 0.5;
		v_shadowMapUv.z = min(spos.z * 0.5 + 0.5, 0.998);
	#endif
	
	v_diffuseUV = a_texCoord;
	v_alphaMapUV.x = 1.0 - pos.x / u_mapSize.x;
	v_alphaMapUV.y = pos.z / u_mapSize.y;
	v_pos = pos.xyz;
	gl_Position = u_projViewTrans * pos;
	
	#if (QUALITY != MIN)
		v_normal = vec3(0.0, 1.0, 0.0);
		v_binormal = vec3(1.0, 0.0, 0.0);
		v_tangent = vec3(0.0, 0.0, 1.0);
		v_viewVec = normalize(u_cameraPosition - pos.xyz);
	#endif
}