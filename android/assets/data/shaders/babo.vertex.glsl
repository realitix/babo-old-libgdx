uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec3 u_cameraPosition;
uniform mat3 u_normalMatrix;
uniform vec4 u_diffuseUVTransform;
#if (QUALITY == MAX)
	uniform mat4 u_shadowMapProjViewTrans;
#endif

attribute vec3 a_position; 
attribute vec2 a_texCoord0;
attribute vec3 a_normal;

varying vec2 v_diffuseUV;
varying vec3 v_pos;
#if (QUALITY != MIN)
	varying vec3 v_viewVec;
	varying vec3 v_normal;
	varying vec3 v_binormal;
	varying vec3 v_tangent;
#endif

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
	
	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
	v_pos = pos.xyz;
	gl_Position = u_projViewTrans * pos;
	
	#if (QUALITY != MIN)
		v_normal = normalize(u_normalMatrix * a_normal);
	
		vec3 c1 = cross(v_normal, vec3(0.0, 0.0, 1.0)); 
		vec3 c2 = cross(v_normal, vec3(0.0, 1.0, 0.0)); 
	
		if(length(c1)>length(c2)) {
			v_tangent = c1;	
		}
		else {
			v_tangent = c2;	
		}
		v_tangent = normalize(v_tangent);
		v_binormal = normalize(cross(v_normal, v_tangent));
		v_viewVec = normalize(u_cameraPosition - pos.xyz);
	#endif
}