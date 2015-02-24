uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_shadowMapProjViewTrans;
uniform vec2 u_mapSize;

attribute vec3 a_position; 
attribute vec2 a_texCoord;

varying vec2 v_diffuseUV;
varying vec2 v_alphaMapUV;
varying vec3 v_shadowMapUv;
varying vec3 v_lightVec; 
varying vec3 v_halfVec;


/**
 Calculer binormal et tangente:
http://www.ozone3d.net/tutorials/mesh_deformer_p2.php#tangent_space
*/

void main() {
	vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	vec4 spos = u_shadowMapProjViewTrans * pos;
	v_shadowMapUv.xy = (spos.xy / spos.w) * 0.5 + 0.5;
	v_shadowMapUv.z = min(spos.z * 0.5 + 0.5, 0.998);
	v_diffuseUV = a_texCoord;
	v_alphaMapUV.x = 1.0 - pos.x / u_mapSize.x;
	v_alphaMapUV.y = pos.z / u_mapSize.y;
	gl_Position = u_projViewTrans * pos;
	
	vec3 n = vec3(0.0, 1.0, 0.0); // Normal
	vec3 t = vec3(0.0, 0.0, 1.0); // Tangent
	vec3 b = vec3(1.0, 0.0, 0.0); // Binormal
	
	vec3 vertexPosition = vec3(pos);
	vec3 lightPosition = vec3(93.0, 5.0, 93.0);
	vec3 lightDir = normalize(lightPosition - vertexPosition);

	v_lightVec.x = dot(lightDir, t);
	v_lightVec.y = dot(lightDir, b);
	v_lightVec.z = dot(lightDir, n);

	vec3 halfVector = normalize(vertexPosition + lightDir);
	v_halfVec.x = dot (halfVector, t);
	v_halfVec.y = dot (halfVector, b);
	v_halfVec.z = dot (halfVector, n);
}